/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.creditdefaultswap.definition;

import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.ObjectUtils;

import com.opengamma.analytics.financial.credit.BuySellProtection;
import com.opengamma.analytics.financial.credit.CreditRating;
import com.opengamma.analytics.financial.credit.DebtSeniority;
import com.opengamma.analytics.financial.credit.Region;
import com.opengamma.analytics.financial.credit.RestructuringClause;
import com.opengamma.analytics.financial.credit.ScheduleGenerationMethod;
import com.opengamma.analytics.financial.credit.Sector;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.frequency.PeriodFrequency;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;

/**
 *  Definition of a vanilla legacy Credit Default Swap contract (i.e. transacted prior to Big Bang of April 2009)
 */
public class CreditDefaultSwapDefinition {

  // ----------------------------------------------------------------------------------------------------------------------------------------

  // Cashflow Conventions are assumed to be as below

  // Notional amount > 0 always - long/short positions are captured by the setting of the 'BuySellProtection' flag

  // Buy protection   -> Pay premium leg, receive contingent leg  -> 'long' protection  -> 'short' credit risk
  // Sell protection  -> Receive premium leg, pay contingent leg  -> 'short' protection -> 'long' credit risk

  // ----------------------------------------------------------------------------------------------------------------------------------------

  // TODO : Allow the case valuationDate = startDate
  // TODO : Allow the case effectiveDate = startDate
  // TODO : Allow the case maturityDate = startDate (e.g. startDate = 21/03/YY, maturityDate = 21/03/YY but IMM adjusted maturityDate = 20/06/YY)
  // TODO : Allow the case valuationDate = maturityDate (should return a MtM of zero)
  // TODO : Allow the case valuationDate = effectiveDate

  // ----------------------------------------------------------------------------------------------------------------------------------------

  // Member variables (all private and final) of the CDS contract (defines what a CDS is)

  // From the users perspective, are we buying or selling protection
  private final BuySellProtection _buySellProtection;

  // Identifiers for the (three) counterparties in the trade
  private final String _protectionBuyer;
  private final String _protectionSeller;
  private final String _referenceEntityTicker;
  private final String _referenceEntityShortName;
  private final String _referenceEntityREDCode;

  // The currency the trade is executed in e.g. USD
  private final Currency _currency;

  // The seniority of the debt of the reference entity the CDS is written on
  private final DebtSeniority _debtSeniority;

  // The restructuring type in the event of a credit event deemed to be a restructuring of the reference entities debt
  private final RestructuringClause _restructuringClause;

  // The composite credit (curve) rating of the reference entity (MarkIt field)
  private final CreditRating _compositeRating;

  // The implied credit rating of the reference entity (MarkIt field)
  private final CreditRating _impliedRating;

  // The industrial classification of the reference entity
  private final Sector _sector;

  // The geographical domicile of the reference entity
  private final Region _region;

  // The country of domicile of the reference entity
  private final String _country;

  // Holiday calendar for the determination of adjusted business days in the cashflow schedule
  private final Calendar _calendar;

  // The date of the contract inception
  private final ZonedDateTime _startDate;

  // The effective date for protection to begin (usually T + 1 for legacy CDS)
  private final ZonedDateTime _effectiveDate;

  // The maturity date of the contract (when premium and protection coverage ceases)
  private final ZonedDateTime _maturityDate;

  // The date on which we want to calculate the CDS MtM
  private final ZonedDateTime _valuationDate;

  // The method for generating the schedule of premium payments
  private final ScheduleGenerationMethod _scheduleGenerationMethod;

  // The frequency of coupon payments (usually quarterly)
  private final PeriodFrequency _couponFrequency;

  // Day-count convention (usually Act/360)
  private final DayCount _daycountFractionConvention;

  // Business day adjustment convention (usually following)
  private final BusinessDayConvention _businessdayAdjustmentConvention;

  // Flag to determine if we business day adjust the final maturity date
  private final boolean _adjustMaturityDate;

  // The trade notional (in the trade currency), convention is that this will always be a positive amount
  private final double _notional;

  // The quoted par spread of the trade (legacy CDS, no exchange of upfront)
  private final double _parSpread;

  // The recovery rate to be used in the calculation of the CDS MtM (can be different to the rate used to calibrate the survival curve)
  private final double _valuationRecoveryRate;

  // The recovery rate to be used when calibrating the hazard rate term structure to the market par CDS spread quotes
  private final double _curveRecoveryRate;

  // Flag to determine whether the accrued coupons should be included in the CDS premium leg calculation
  private final boolean _includeAccruedPremium;

  // The credit key to uniquely identify a reference entities par spread CDS curve
  private final String _creditKey;

  // ----------------------------------------------------------------------------------------------------------------------------------------

  // Constructor for a CDS contract object

  public CreditDefaultSwapDefinition(BuySellProtection buySellProtection,
      String protectionBuyer,
      String protectionSeller,
      String referenceEntityTicker,
      String referenceEntityShortName,
      String referenceEntityREDCode,
      Currency currency,
      DebtSeniority debtSeniority,
      RestructuringClause restructuringClause,
      CreditRating compositeRating,
      CreditRating impliedRating,
      Sector sector,
      Region region,
      String country,
      Calendar calendar,
      ZonedDateTime startDate,
      ZonedDateTime effectiveDate,
      ZonedDateTime maturityDate,
      ZonedDateTime valuationDate,
      ScheduleGenerationMethod scheduleGenerationMethod,
      PeriodFrequency couponFrequency,
      DayCount daycountFractionConvention,
      BusinessDayConvention businessdayAdjustmentConvention,
      boolean adjustMaturityDate,
      double notional,
      double parSpread,
      double valuationRecoveryRate,
      double curveRecoveryRate,
      boolean includeAccruedPremium) {

    // ------------------------------------------------------------------------------------------------

    // Check the validity of the input arguments

    ArgumentChecker.notNull(buySellProtection, "Buy/Sell field is null");

    ArgumentChecker.notNull(protectionBuyer, "Protection buyer field is null");
    ArgumentChecker.isFalse(protectionBuyer.isEmpty(), "Protection buyer field is empty");

    ArgumentChecker.notNull(protectionSeller, "Protection seller field is null");
    ArgumentChecker.isFalse(protectionSeller.isEmpty(), "Protection seller field is empty");

    ArgumentChecker.notNull(referenceEntityTicker, "Reference entity ticker field is null");
    ArgumentChecker.isFalse(referenceEntityTicker.isEmpty(), "Reference entity ticker field is empty");

    ArgumentChecker.notNull(referenceEntityShortName, "Reference entity short name field is null");
    ArgumentChecker.isFalse(referenceEntityShortName.isEmpty(), "Reference entity short name field is empty");

    ArgumentChecker.notNull(referenceEntityREDCode, "Reference entity RED code field is null");
    ArgumentChecker.isFalse(referenceEntityREDCode.isEmpty(), "Reference entity RED code field is empty");

    ArgumentChecker.notNull(currency, "Currency field is null");
    ArgumentChecker.notNull(debtSeniority, "Debt seniority field is null");
    ArgumentChecker.notNull(restructuringClause, "Restructuring clause field is null");
    ArgumentChecker.notNull(compositeRating, "Composite rating field is null");
    ArgumentChecker.notNull(impliedRating, "Implied rating field is null");
    ArgumentChecker.notNull(sector, "Sector field is null");
    ArgumentChecker.notNull(region, "Region field is null");

    ArgumentChecker.notNull(country, "Country field is null");
    ArgumentChecker.isFalse(country.isEmpty(), "Country field is empty");

    ArgumentChecker.notNull(calendar, "Calendar field is null");

    ArgumentChecker.notNull(startDate, "Start date field is null");
    ArgumentChecker.notNull(effectiveDate, "Effective date field is null");
    ArgumentChecker.notNull(maturityDate, "Maturity date field is null");
    ArgumentChecker.notNull(valuationDate, "Valuation date field is null");

    // Check the temporal ordering of the input dates (these are the unadjusted dates entered by the user)
    ArgumentChecker.isTrue(startDate.isBefore(valuationDate), "Start date {} must be before valuation date {}", startDate, valuationDate);
    ArgumentChecker.isTrue(startDate.isBefore(effectiveDate), "Start date {} must be before effective date {}", startDate, effectiveDate);
    ArgumentChecker.isTrue(startDate.isBefore(maturityDate), "Start date {} must be before maturity date {}", startDate, maturityDate);
    ArgumentChecker.isTrue(valuationDate.isBefore(maturityDate), "Valuation date {} must be before maturity date {}", valuationDate, maturityDate);
    ArgumentChecker.isTrue(valuationDate.isAfter(effectiveDate), "Valuation date {} must be after effective date {}", valuationDate, effectiveDate);

    ArgumentChecker.notNull(scheduleGenerationMethod, "Schedule generation method field is null");
    ArgumentChecker.notNull(couponFrequency, "Coupon frequency field is null");
    ArgumentChecker.notNull(daycountFractionConvention, "Daycount convention field is null");
    ArgumentChecker.notNull(businessdayAdjustmentConvention, "Business day adjustment convention field is null");

    ArgumentChecker.isTrue(notional >= 0.0, "Notional amount should be greater than or equal to zero");
    ArgumentChecker.isTrue(parSpread >= 0.0, "CDS par spread should be greater than or equal to zero");

    ArgumentChecker.isTrue(valuationRecoveryRate >= 0.0, "Valuation recovery rate should be greater than or equal to 0%");
    ArgumentChecker.isTrue(valuationRecoveryRate <= 1.0, "Valuation recovery rate should be less than or equal to 100%");

    ArgumentChecker.isTrue(curveRecoveryRate >= 0.0, "Curve recovery rate should be greater than or equal to 0%");
    ArgumentChecker.isTrue(curveRecoveryRate <= 1.0, "Curve recovery rate should be less than or equal to 100%");

    // ------------------------------------------------------------------------------------------------

    // Assign the member variables for the CDS object

    _buySellProtection = buySellProtection;
    _protectionBuyer = protectionBuyer;
    _protectionSeller = protectionSeller;
    _referenceEntityTicker = referenceEntityTicker;
    _referenceEntityShortName = referenceEntityShortName;
    _referenceEntityREDCode = referenceEntityREDCode;

    _currency = currency;
    _debtSeniority = debtSeniority;
    _restructuringClause = restructuringClause;

    _compositeRating = compositeRating;
    _impliedRating = impliedRating;

    _sector = sector;
    _region = region;
    _country = country;

    _calendar = calendar;

    _startDate = startDate;
    _effectiveDate = effectiveDate;
    _maturityDate = maturityDate;
    _valuationDate = valuationDate;

    _scheduleGenerationMethod = scheduleGenerationMethod;
    _couponFrequency = couponFrequency;
    _daycountFractionConvention = daycountFractionConvention;
    _businessdayAdjustmentConvention = businessdayAdjustmentConvention;
    _adjustMaturityDate = adjustMaturityDate;

    _notional = notional;
    _parSpread = parSpread;

    _valuationRecoveryRate = valuationRecoveryRate;
    _curveRecoveryRate = curveRecoveryRate;

    _includeAccruedPremium = includeAccruedPremium;

    // REVIEW 29/8/2012 think about using UniqueId instead of _creditKey
    _creditKey = _referenceEntityTicker + "_" + _currency + "_" + _debtSeniority + "_" + _restructuringClause;

    // ------------------------------------------------------------------------------------------------
  }

  // ----------------------------------------------------------------------------------------------------------------------------------------

  // Member variable accessor functions (all public)

  public BuySellProtection getBuySellProtection() {
    return _buySellProtection;
  }

  public String getProtectionBuyer() {
    return _protectionBuyer;
  }

  public String getProtectionSeller() {
    return _protectionSeller;
  }

  public String getReferenceEntityTicker() {
    return _referenceEntityTicker;
  }

  public String getReferenceEntityShortName() {
    return _referenceEntityShortName;
  }

  public String getReferenceEntityREDCode() {
    return _referenceEntityREDCode;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------

  public Currency getCurrency() {
    return _currency;
  }

  public DebtSeniority getDebtSeniority() {
    return _debtSeniority;
  }

  public RestructuringClause getRestructuringClause() {
    return _restructuringClause;
  }

  public CreditRating getCompositeRating() {
    return _compositeRating;
  }

  public CreditRating getImpliedRating() {
    return _impliedRating;
  }

  public Sector getSector() {
    return _sector;
  }

  public Region getRegion() {
    return _region;
  }

  public String getCountry() {
    return _country;
  }

  public Calendar getCalendar() {
    return _calendar;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------

  public ZonedDateTime getStartDate() {
    return _startDate;
  }

  public ZonedDateTime getEffectiveDate() {
    return _effectiveDate;
  }

  public ZonedDateTime getMaturityDate() {
    return _maturityDate;
  }

  public ZonedDateTime getValuationDate() {
    return _valuationDate;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------

  public ScheduleGenerationMethod getScheduleGenerationMethod() {
    return _scheduleGenerationMethod;
  }

  public PeriodFrequency getCouponFrequency() {
    return _couponFrequency;
  }

  public DayCount getDayCountFractionConvention() {
    return _daycountFractionConvention;
  }

  public BusinessDayConvention getBusinessDayAdjustmentConvention() {
    return _businessdayAdjustmentConvention;
  }

  public boolean getAdjustMaturityDate() {
    return _adjustMaturityDate;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------

  public double getNotional() {
    return _notional;
  }

  public double getParSpread() {
    return _parSpread;
  }

  public double getValuationRecoveryRate() {
    return _valuationRecoveryRate;
  }

  public double getCurveRecoveryRate() {
    return _curveRecoveryRate;
  }

  public boolean getIncludeAccruedPremium() {
    return _includeAccruedPremium;
  }

  //----------------------------------------------------------------------------------------------------------------------------------------

  public String getCreditKey() {
    return _creditKey;
  }

  // ----------------------------------------------------------------------------------------------------------------------------------------

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (_adjustMaturityDate ? 1231 : 1237);
    result = prime * result + ((_calendar == null) ? 0 : _calendar.hashCode());
    long temp;
    temp = Double.doubleToLongBits(_curveRecoveryRate);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + (_includeAccruedPremium ? 1231 : 1237);
    temp = Double.doubleToLongBits(_notional);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_parSpread);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_valuationRecoveryRate);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  // ----------------------------------------------------------------------------------------------------------------------------------------

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    CreditDefaultSwapDefinition other = (CreditDefaultSwapDefinition) obj;

    if (_buySellProtection != other._buySellProtection) {
      return false;
    }

    if (_adjustMaturityDate != other._adjustMaturityDate) {
      return false;
    }

    if (!ObjectUtils.equals(_businessdayAdjustmentConvention, other._businessdayAdjustmentConvention)) {
      return false;
    }

    if (!ObjectUtils.equals(_calendar, other._calendar)) {
      return false;
    }

    if (_compositeRating != other._compositeRating) {
      return false;
    }

    if (!ObjectUtils.equals(_country, other._country)) {
      return false;
    }

    if (!ObjectUtils.equals(_couponFrequency, other._couponFrequency)) {
      return false;
    }

    if (!ObjectUtils.equals(_creditKey, other._creditKey)) {
      return false;
    }

    if (!ObjectUtils.equals(_currency, other._currency)) {
      return false;
    }

    if (Double.doubleToLongBits(_curveRecoveryRate) != Double.doubleToLongBits(other._curveRecoveryRate)) {
      return false;
    }

    if (!ObjectUtils.equals(_daycountFractionConvention, other._daycountFractionConvention)) {
      return false;
    }

    if (_debtSeniority != other._debtSeniority) {
      return false;
    }

    if (!ObjectUtils.equals(_effectiveDate, other._effectiveDate)) {
      return false;
    }

    if (_impliedRating != other._impliedRating) {
      return false;
    }

    if (_includeAccruedPremium != other._includeAccruedPremium) {
      return false;
    }

    if (!ObjectUtils.equals(_maturityDate, other._maturityDate)) {
      return false;
    }

    if (Double.doubleToLongBits(_notional) != Double.doubleToLongBits(other._notional)) {
      return false;
    }

    if (Double.doubleToLongBits(_parSpread) != Double.doubleToLongBits(other._parSpread)) {
      return false;
    }

    if (!ObjectUtils.equals(_protectionBuyer, other._protectionBuyer)) {
      return false;
    }

    if (!ObjectUtils.equals(_protectionSeller, other._protectionSeller)) {
      return false;
    }

    if (!ObjectUtils.equals(_referenceEntityREDCode, other._referenceEntityREDCode)) {
      return false;
    }

    if (!ObjectUtils.equals(_referenceEntityShortName, other._referenceEntityShortName)) {
      return false;
    }

    if (!ObjectUtils.equals(_referenceEntityTicker, other._referenceEntityTicker)) {
      return false;
    }

    if (_region != other._region) {
      return false;
    }

    if (_restructuringClause != other._restructuringClause) {
      return false;
    }

    if (_scheduleGenerationMethod != other._scheduleGenerationMethod) {
      return false;
    }

    if (_sector != other._sector) {
      return false;
    }

    if (!ObjectUtils.equals(_startDate, other._startDate)) {
      return false;
    }

    if (!ObjectUtils.equals(_valuationDate, other._valuationDate)) {
      return false;
    }

    if (Double.doubleToLongBits(_valuationRecoveryRate) != Double.doubleToLongBits(other._valuationRecoveryRate)) {
      return false;
    }

    return true;
  }

  //---------------------------------------------------------------------------------------------------------------------------------------- 
}

//----------------------------------------------------------------------------------------------------------------------------------------