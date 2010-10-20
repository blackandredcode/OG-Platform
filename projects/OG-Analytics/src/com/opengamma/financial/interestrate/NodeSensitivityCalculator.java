/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.math.curve.InterpolatedDoublesCurve;
import com.opengamma.math.interpolation.CombinedInterpolatorExtrapolator;
import com.opengamma.math.interpolation.Interpolator1D;
import com.opengamma.math.interpolation.Interpolator1DFactory;
import com.opengamma.math.interpolation.data.Interpolator1DDataBundle;
import com.opengamma.math.interpolation.sensitivity.CombinedInterpolatorExtrapolatorNodeSensitivityCalculatorFactory;
import com.opengamma.math.interpolation.sensitivity.Interpolator1DNodeSensitivityCalculator;
import com.opengamma.math.interpolation.sensitivity.Interpolator1DNodeSensitivityCalculatorFactory;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.util.tuple.DoublesPair;

/**
 * 
 */
public class NodeSensitivityCalculator {

  public DoubleMatrix1D presentValueCalculate(final InterestRateDerivative ird, final YieldCurveBundle fixedCurves, final LinkedHashMap<String, YieldAndDiscountCurve> interpolatedCurves) {
    return calculate(ird, PresentValueSensitivityCalculator.getInstance(), fixedCurves, interpolatedCurves);
  }

  public DoubleMatrix1D parRateCalculate(final InterestRateDerivative ird, final YieldCurveBundle fixedCurves, final LinkedHashMap<String, YieldAndDiscountCurve> interpolatedCurves) {
    return calculate(ird, ParRateCurveSensitivityCalculator.getInstance(), fixedCurves, interpolatedCurves);
  }

  public DoubleMatrix1D calculate(final InterestRateDerivative ird, final InterestRateDerivativeVisitor<YieldCurveBundle, Map<String, List<DoublesPair>>> calculator,
      final YieldCurveBundle fixedCurves, final LinkedHashMap<String, YieldAndDiscountCurve> interpolatedCurves) {

    Validate.notNull(ird, "null InterestRateDerivative");
    Validate.notNull(calculator, "null calculator");
    Validate.notNull(interpolatedCurves, "interpolated curves");

    final YieldCurveBundle allCurves = new YieldCurveBundle(interpolatedCurves);

    if (fixedCurves != null) {
      for (final String name : interpolatedCurves.keySet()) {
        Validate.isTrue(!fixedCurves.containsName(name), "fixed curves contain a name that is also is interpolated curves");
      }
      allCurves.addAll(fixedCurves);
    }

    final Map<String, List<DoublesPair>> senseMap = calculator.getValue(ird, allCurves);

    return curveToNodeSensitivities(senseMap, interpolatedCurves);

  }

  @SuppressWarnings("unchecked")
  public DoubleMatrix1D curveToNodeSensitivities(final Map<String, List<DoublesPair>> curveSenitivities, final LinkedHashMap<String, YieldAndDiscountCurve> interpolatedCurves) {

    final List<Double> res = new ArrayList<Double>();
    for (final String name : interpolatedCurves.keySet()) { // loop over all curves (by name)
      final YieldAndDiscountCurve curve = interpolatedCurves.get(name);
      if (!(curve.getCurve() instanceof InterpolatedDoublesCurve)) {
        throw new IllegalArgumentException("Can only handle interpolated curves at the moment");
      }
      final InterpolatedDoublesCurve interpolatedCurve = (InterpolatedDoublesCurve) curve.getCurve();
      final Interpolator1D<? extends Interpolator1DDataBundle> interpolator = interpolatedCurve.getInterpolator();
      final Interpolator1DDataBundle data = interpolatedCurve.getDataBundle();
      Interpolator1DNodeSensitivityCalculator sensitivityCalculator;
      // TODO move this logic into a factory
      if (interpolator instanceof CombinedInterpolatorExtrapolator) {
        final CombinedInterpolatorExtrapolator combined = (CombinedInterpolatorExtrapolator) interpolator;
        final String interpolatorName = Interpolator1DFactory.getInterpolatorName(combined.getInterpolator());
        final String leftExtrapolatorName = Interpolator1DFactory.getInterpolatorName(combined.getLeftExtrapolator());
        final String rightExtrapolatorName = Interpolator1DFactory.getInterpolatorName(combined.getRightExtrapolator());
        sensitivityCalculator = CombinedInterpolatorExtrapolatorNodeSensitivityCalculatorFactory.getSensitivityCalculator(interpolatorName, leftExtrapolatorName, rightExtrapolatorName, false);
      } else {
        final String interpolatorName = Interpolator1DFactory.getInterpolatorName(interpolator);
        sensitivityCalculator = Interpolator1DNodeSensitivityCalculatorFactory.getSensitivityCalculator(interpolatorName, false);
      }
      final List<DoublesPair> senseList = curveSenitivities.get(name);
      final double[][] sensitivity = new double[senseList.size()][];
      int k = 0;
      for (final DoublesPair timeAndDF : senseList) {
        sensitivity[k++] = sensitivityCalculator.calculate(data, timeAndDF.getFirst());
      }
      for (int j = 0; j < sensitivity[0].length; j++) {
        double temp = 0.0;
        k = 0;
        for (final DoublesPair timeAndDF : senseList) {
          temp += timeAndDF.getSecond() * sensitivity[k++][j];
        }
        res.add(temp);
      }
    }

    return new DoubleMatrix1D(res.toArray(new Double[0]));

  }

  @SuppressWarnings("unchecked")
  public DoubleMatrix1D curveToNodeSensitivities(final List<DoublesPair> curveSenitivities, final YieldAndDiscountCurve yieldCurve) {
    if (!(yieldCurve.getCurve() instanceof InterpolatedDoublesCurve)) {
      throw new IllegalArgumentException("Can only handle InterpolatedDoublesCurve");
    }
    final InterpolatedDoublesCurve interpolatedCurve = (InterpolatedDoublesCurve) yieldCurve.getCurve();
    final double[] res = new double[interpolatedCurve.size()];
    final Interpolator1D<? extends Interpolator1DDataBundle> interpolator = interpolatedCurve.getInterpolator();
    final Interpolator1DDataBundle data = interpolatedCurve.getDataBundle();
    Interpolator1DNodeSensitivityCalculator sensitivityCalculator;
    // TODO move this logic into a factory
    if (interpolator instanceof CombinedInterpolatorExtrapolator) {
      final CombinedInterpolatorExtrapolator combined = (CombinedInterpolatorExtrapolator) interpolator;
      final String interpolatorName = Interpolator1DFactory.getInterpolatorName(combined.getInterpolator());
      final String leftExtrapolatorName = Interpolator1DFactory.getInterpolatorName(combined.getLeftExtrapolator());
      final String rightExtrapolatorName = Interpolator1DFactory.getInterpolatorName(combined.getRightExtrapolator());
      sensitivityCalculator = CombinedInterpolatorExtrapolatorNodeSensitivityCalculatorFactory.getSensitivityCalculator(interpolatorName, leftExtrapolatorName, rightExtrapolatorName, false);
    } else {
      final String interpolatorName = Interpolator1DFactory.getInterpolatorName(interpolator);
      sensitivityCalculator = Interpolator1DNodeSensitivityCalculatorFactory.getSensitivityCalculator(interpolatorName, false);
    }

    final double[][] sensitivity = new double[curveSenitivities.size()][];
    int k = 0;
    for (final DoublesPair timeAndDF : curveSenitivities) {
      sensitivity[k++] = sensitivityCalculator.calculate(data, timeAndDF.getFirst());
    }
    for (int j = 0; j < sensitivity[0].length; j++) {
      double temp = 0.0;
      k = 0;
      for (final DoublesPair timeAndDF : curveSenitivities) {
        temp += timeAndDF.getSecond() * sensitivity[k++][j];
      }
      res[j] = temp;
    }
    return new DoubleMatrix1D(res);
  }

}
