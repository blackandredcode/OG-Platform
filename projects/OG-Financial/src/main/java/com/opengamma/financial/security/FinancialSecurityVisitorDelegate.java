/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security;

import com.opengamma.financial.security.bond.CorporateBondSecurity;
import com.opengamma.financial.security.bond.GovernmentBondSecurity;
import com.opengamma.financial.security.bond.MunicipalBondSecurity;
import com.opengamma.financial.security.capfloor.CapFloorCMSSpreadSecurity;
import com.opengamma.financial.security.capfloor.CapFloorSecurity;
import com.opengamma.financial.security.cash.CashSecurity;
import com.opengamma.financial.security.cds.CDSSecurity;
import com.opengamma.financial.security.cds.LegacyFixedRecoveryCDSSecurity;
import com.opengamma.financial.security.cds.LegacyRecoveryLockCDSSecurity;
import com.opengamma.financial.security.cds.LegacyVanillaCDSSecurity;
import com.opengamma.financial.security.cds.StandardFixedRecoveryCDSSecurity;
import com.opengamma.financial.security.cds.StandardRecoveryLockCDSSecurity;
import com.opengamma.financial.security.cds.StandardVanillaCDSSecurity;
import com.opengamma.financial.security.deposit.ContinuousZeroDepositSecurity;
import com.opengamma.financial.security.deposit.PeriodicZeroDepositSecurity;
import com.opengamma.financial.security.deposit.SimpleZeroDepositSecurity;
import com.opengamma.financial.security.equity.EquitySecurity;
import com.opengamma.financial.security.equity.EquityVarianceSwapSecurity;
import com.opengamma.financial.security.forward.AgricultureForwardSecurity;
import com.opengamma.financial.security.forward.EnergyForwardSecurity;
import com.opengamma.financial.security.forward.MetalForwardSecurity;
import com.opengamma.financial.security.fra.FRASecurity;
import com.opengamma.financial.security.future.AgricultureFutureSecurity;
import com.opengamma.financial.security.future.BondFutureSecurity;
import com.opengamma.financial.security.future.EnergyFutureSecurity;
import com.opengamma.financial.security.future.EquityFutureSecurity;
import com.opengamma.financial.security.future.EquityIndexDividendFutureSecurity;
import com.opengamma.financial.security.future.FXFutureSecurity;
import com.opengamma.financial.security.future.IndexFutureSecurity;
import com.opengamma.financial.security.future.InterestRateFutureSecurity;
import com.opengamma.financial.security.future.MetalFutureSecurity;
import com.opengamma.financial.security.future.StockFutureSecurity;
import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.financial.security.fx.NonDeliverableFXForwardSecurity;
import com.opengamma.financial.security.option.BondFutureOptionSecurity;
import com.opengamma.financial.security.option.CommodityFutureOptionSecurity;
import com.opengamma.financial.security.option.EquityBarrierOptionSecurity;
import com.opengamma.financial.security.option.EquityIndexDividendFutureOptionSecurity;
import com.opengamma.financial.security.option.EquityIndexOptionSecurity;
import com.opengamma.financial.security.option.EquityOptionSecurity;
import com.opengamma.financial.security.option.FXBarrierOptionSecurity;
import com.opengamma.financial.security.option.FXDigitalOptionSecurity;
import com.opengamma.financial.security.option.FXOptionSecurity;
import com.opengamma.financial.security.option.IRFutureOptionSecurity;
import com.opengamma.financial.security.option.NonDeliverableFXDigitalOptionSecurity;
import com.opengamma.financial.security.option.NonDeliverableFXOptionSecurity;
import com.opengamma.financial.security.option.SwaptionSecurity;
import com.opengamma.financial.security.swap.ForwardSwapSecurity;
import com.opengamma.financial.security.swap.SwapSecurity;

/**
 * Adapter for visiting all concrete asset classes.
 *
 * @param <T> Return type for visitor.
 */
class FinancialSecurityVisitorDelegate<T> implements FinancialSecurityVisitor<T> {

  private final FinancialSecurityVisitor<T> _delegate;

  public FinancialSecurityVisitorDelegate(final FinancialSecurityVisitor<T> delegate) {
    _delegate = delegate;
  }

  @Override
  public T visitAgricultureFutureSecurity(final AgricultureFutureSecurity security) {
    return _delegate.visitAgricultureFutureSecurity(security);
  }

  @Override
  public T visitCorporateBondSecurity(final CorporateBondSecurity security) {
    return _delegate.visitCorporateBondSecurity(security);
  }

  @Override
  public T visitGovernmentBondSecurity(final GovernmentBondSecurity security) {
    return _delegate.visitGovernmentBondSecurity(security);
  }

  @Override
  public T visitMunicipalBondSecurity(final MunicipalBondSecurity security) {
    return _delegate.visitMunicipalBondSecurity(security);
  }

  @Override
  public T visitCapFloorCMSSpreadSecurity(final CapFloorCMSSpreadSecurity security) {
    return _delegate.visitCapFloorCMSSpreadSecurity(security);
  }

  @Override
  public T visitCapFloorSecurity(final CapFloorSecurity security) {
    return _delegate.visitCapFloorSecurity(security);
  }

  @Override
  public T visitCashSecurity(final CashSecurity security) {
    return _delegate.visitCashSecurity(security);
  }

  @Override
  public T visitContinuousZeroDepositSecurity(final ContinuousZeroDepositSecurity security) {
    return _delegate.visitContinuousZeroDepositSecurity(security);
  }

  @Override
  public T visitEquityBarrierOptionSecurity(final EquityBarrierOptionSecurity security) {
    return _delegate.visitEquityBarrierOptionSecurity(security);
  }

  @Override
  public T visitEquityIndexDividendFutureOptionSecurity(final EquityIndexDividendFutureOptionSecurity security) {
    return _delegate.visitEquityIndexDividendFutureOptionSecurity(security);
  }

  @Override
  public T visitEquityIndexOptionSecurity(final EquityIndexOptionSecurity security) {
    return _delegate.visitEquityIndexOptionSecurity(security);
  }

  @Override
  public T visitEquityOptionSecurity(final EquityOptionSecurity security) {
    return _delegate.visitEquityOptionSecurity(security);
  }

  @Override
  public T visitEquitySecurity(final EquitySecurity security) {
    return _delegate.visitEquitySecurity(security);
  }

  @Override
  public T visitEquityVarianceSwapSecurity(final EquityVarianceSwapSecurity security) {
    return _delegate.visitEquityVarianceSwapSecurity(security);
  }

  @Override
  public T visitFRASecurity(final FRASecurity security) {
    return _delegate.visitFRASecurity(security);
  }

  @Override
  public T visitFXBarrierOptionSecurity(final FXBarrierOptionSecurity security) {
    return _delegate.visitFXBarrierOptionSecurity(security);
  }

  @Override
  public T visitFXDigitalOptionSecurity(final FXDigitalOptionSecurity security) {
    return _delegate.visitFXDigitalOptionSecurity(security);
  }

  @Override
  public T visitFXForwardSecurity(final FXForwardSecurity security) {
    return _delegate.visitFXForwardSecurity(security);
  }

  @Override
  public T visitFXOptionSecurity(final FXOptionSecurity security) {
    return _delegate.visitFXOptionSecurity(security);
  }

  @Override
  public T visitIRFutureOptionSecurity(final IRFutureOptionSecurity security) {
    return _delegate.visitIRFutureOptionSecurity(security);
  }

  @Override
  public T visitInterestRateFutureSecurity(final InterestRateFutureSecurity security) {
    return _delegate.visitInterestRateFutureSecurity(security);
  }

  @Override
  public T visitNonDeliverableFXDigitalOptionSecurity(final NonDeliverableFXDigitalOptionSecurity security) {
    return _delegate.visitNonDeliverableFXDigitalOptionSecurity(security);
  }

  @Override
  public T visitNonDeliverableFXForwardSecurity(final NonDeliverableFXForwardSecurity security) {
    return _delegate.visitNonDeliverableFXForwardSecurity(security);
  }

  @Override
  public T visitNonDeliverableFXOptionSecurity(final NonDeliverableFXOptionSecurity security) {
    return _delegate.visitNonDeliverableFXOptionSecurity(security);
  }

  @Override
  public T visitPeriodicZeroDepositSecurity(final PeriodicZeroDepositSecurity security) {
    return _delegate.visitPeriodicZeroDepositSecurity(security);
  }

  @Override
  public T visitSimpleZeroDepositSecurity(final SimpleZeroDepositSecurity security) {
    return _delegate.visitSimpleZeroDepositSecurity(security);
  }

  @Override
  public T visitForwardSwapSecurity(final ForwardSwapSecurity security) {
    return _delegate.visitForwardSwapSecurity(security);
  }

  @Override
  public T visitSwapSecurity(final SwapSecurity security) {
    return _delegate.visitSwapSecurity(security);
  }

  @Override
  public T visitSwaptionSecurity(final SwaptionSecurity security) {
    return _delegate.visitSwaptionSecurity(security);
  }

  @Override
  public T visitBondFutureSecurity(final BondFutureSecurity security) {
    return _delegate.visitBondFutureSecurity(security);
  }

  @Override
  public T visitCommodityFutureOptionSecurity(final CommodityFutureOptionSecurity security) {
    return _delegate.visitCommodityFutureOptionSecurity(security);
  }

  @Override
  public T visitBondFutureOptionSecurity(final BondFutureOptionSecurity security) {
    return _delegate.visitBondFutureOptionSecurity(security);
  }

  @Override
  public T visitEnergyFutureSecurity(final EnergyFutureSecurity security) {
    return _delegate.visitEnergyFutureSecurity(security);
  }

  @Override
  public T visitEquityFutureSecurity(final EquityFutureSecurity security) {
    return _delegate.visitEquityFutureSecurity(security);
  }

  @Override
  public T visitEquityIndexDividendFutureSecurity(final EquityIndexDividendFutureSecurity security) {
    return _delegate.visitEquityIndexDividendFutureSecurity(security);
  }

  @Override
  public T visitFXFutureSecurity(final FXFutureSecurity security) {
    return _delegate.visitFXFutureSecurity(security);
  }

  @Override
  public T visitIndexFutureSecurity(final IndexFutureSecurity security) {
    return _delegate.visitIndexFutureSecurity(security);
  }

  @Override
  public T visitMetalFutureSecurity(final MetalFutureSecurity security) {
    return _delegate.visitMetalFutureSecurity(security);
  }

  @Override
  public T visitStockFutureSecurity(final StockFutureSecurity security) {
    return _delegate.visitStockFutureSecurity(security);
  }

  @Override
  public T visitAgricultureForwardSecurity(final AgricultureForwardSecurity security) {
    return _delegate.visitAgricultureForwardSecurity(security);
  }

  @Override
  public T visitEnergyForwardSecurity(final EnergyForwardSecurity security) {
    return _delegate.visitEnergyForwardSecurity(security);
  }

  @Override
  public T visitMetalForwardSecurity(final MetalForwardSecurity security) {
    return _delegate.visitMetalForwardSecurity(security);
  }

  @Override
  public T visitCDSSecurity(final CDSSecurity security) {
    return _delegate.visitCDSSecurity(security);
  }

  @Override
  public T visitStandardVanillaCDSSecurity(final StandardVanillaCDSSecurity security) {
    return _delegate.visitStandardVanillaCDSSecurity(security);
  }

  @Override
  public T visitStandardFixedRecoveryCDSSecurity(final StandardFixedRecoveryCDSSecurity security) {
    return _delegate.visitStandardFixedRecoveryCDSSecurity(security);
  }

  @Override
  public T visitStandardRecoveryLockCDSSecurity(final StandardRecoveryLockCDSSecurity security) {
    return _delegate.visitStandardRecoveryLockCDSSecurity(security);
  }

  @Override
  public T visitLegacyVanillaCDSSecurity(final LegacyVanillaCDSSecurity security) {
    return _delegate.visitLegacyVanillaCDSSecurity(security);
  }

  @Override
  public T visitLegacyFixedRecoveryCDSSecurity(final LegacyFixedRecoveryCDSSecurity security) {
    return _delegate.visitLegacyFixedRecoveryCDSSecurity(security);
  }

  @Override
  public T visitLegacyRecoveryLockCDSSecurity(final LegacyRecoveryLockCDSSecurity security) {
    return _delegate.visitLegacyRecoveryLockCDSSecurity(security);
  }
}
