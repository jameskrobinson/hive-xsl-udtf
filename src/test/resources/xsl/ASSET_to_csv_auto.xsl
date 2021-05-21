<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
<xsl:strip-space elements='*' />
<xsl:param name='COL_SEP' select='"&#09;"' />
<xsl:param name='ROW_SEP' select='"&#10;"' />
<xsl:param name='DEEP_COPY' select='"YES"' />
			<!--HiveHeaders:dmAssetId:string, dmOwnerTable:string, TradeId:string, SchedEvents:string, Type:string, SubType:string, RC:string, Book:string, PorS:string, EffDate:string, MatDate:string, Notional:string, NotExp:string, Ccy:string, STUB_Date1:string, STUB_Rate1:string, STUB_Date2:string, STUB_Rate2:string, STUB_StubType:string, STUB_Decomp:string, STUB_CompMode:string, STUB_Rounding:string, STUB_InterpStyle:string, STUB_StartTerm1:string, STUB_StartTerm2:string, STUB_EndTerm1:string, STUB_EndTerm2:string, ResetInStub:string, INTEREST_IndexForm:string, INTEREST_YldFreq:string, INTEREST_FixFloat:string, INTEREST_Rate:string, INTEREST_Basis:string, INTEREST_dmIndex:string, INTEREST_Ccy:string, INT_REF_OrgSource:string, INT_REF_Source:string, INT_REF_NNearBy:string, INTEREST_Spread:string, INTEREST_Term:string, INT_AVG_Type:string, INT_AVG_Freq:string, INT_AVG_Compound:string, INT_AVG_AnnDay:string, INT_AVG_Rule:string, INT_AVG_Spread:string, INT_AVG_Time:string, INT_AVG_CutOffDays:string, INT_AVG_CorBGap:string, INTEREST_DType:string, INTEREST_Round:string, INTEREST_RoundDP:string, INT_RES_Rule:string, INT_RES_Time:string, INT_RES_Round:string, INT_RES_RoundDP:string, INT_RES_NTL_REF_OrgSource:string, INT_RES_NTL_REF_Source:string, INT_RES_NTL_REF_NNearBy:string, INT_RES_NTL_Cal:string, INT_RES_NTL_Term:string, INT_RES_FX_REF_OrgSource:string, INT_RES_FX_REF_Source:string, INT_RES_FX_REF_NNearBy:string, INT_RES_FX_Cal:string, INT_RES_FX_Term:string, INT_RES_FxDomain:string, INT_ACC_AccCalc:string, INT_ACC_Method:string, INT_ACC_ForL:string, INT_ACC_Basis:string, INTEREST_FwdDecomp:string, INT_DIV_Ratio:string, INT_DIV_ReInvest:string, INT_DIV_DivMethod:string, INTEREST_NRate:string, INTEREST_NonContig:string, INTEREST_UnderIndex:string, SCHED_Pay_Freq:string, SCHED_Pay_AnnDay:string, SCHED_Pay_Rule:string, SCHED_Pay_IntRule:string, SCHED_Pay_Time:string, SCHED_Pay_Cal:string, SCHED_Pay_DRule:string, SCHED_Reset_Freq:string, SCHED_Reset_AnnDay:string, SCHED_Reset_Rule:string, SCHED_Reset_IntRule:string, SCHED_Reset_Time:string, SCHED_Reset_Cal:string, SCHED_Reset_DRule:string, COMP_CompFreq:string, COMP_Mode:string, AMORT_Type:string, AMORT_Amount:string, AMORT_Start:string, AMORT_End:string, AMORT_Freq:string, Custom:string, CROSS_CCY_NotExch:string, CROSS_CCY_ForexRate:string, OptionData:string, PModel:string, Style:string, OPForm:string, SubIndex:string, TermDate:string, ROLL_RollNature:string, ROLL_NoticeDays:string, ROLL_ReInvestAmt:string, FstCpEffect:string, NextFixing:string, RiskId:string, INT_AVG_Conversion:string, INT_RES_RefixMode:string, INT_AVG_CutOffMeth:string, Audit_Version:string, AMORT_NtlForm:string, AMORT_Sched_Freq:string, AMORT_Sched_AnnDay:string, AMORT_Sched_Rule:string, AMORT_Sched_IntRule:string, AMORT_Sched_Time:string, AMORT_Sched_Cal:string, AMORT_Sched_DRuleID:string, INTEREST_FundIndex:string, INTEREST_FundSprd:string, MMType:string, LegRole:string, GroupNum:string, NumEve:string, Formula:string, Events:string, CmdtyQuoteType:string, INT_DIV_PayDividend:string, INT_DIV_HedgeDivRatio:string, INT_RES_AuxDomain:string, SCHED_Pay_GapDRule:string, SCHED_Reset_GapDRule:string, AMORT_Sched_GapDRuleID:string, AMORT_DRuleID:string, INT_AVG_Gap:string, INT_RES_NTL_Gap:string, INT_RES_FX_Gap:string, SCHED_Pay_Gap:string, SCHED_Reset_Gap:string, AMORT_Sched_Gap:string, INT_DIV_Gap:string, ProductName:string, AccrualFactors:string, STUB_StartCal:string, STUB_EndCal:string, STUB_StartSource1:string, STUB_StartSource2:string, STUB_EndSource1:string, STUB_EndSource2:string, INT_InterimRound:string, INT_InterimRoundDP:string, INT_RES_ConvRound:string, INT_RES_ConvRoundDP:string, INT_RES_PaymentReset:string, INT_RES_CutOffDays:string, INT_RES_NTL_PaymentReset:string, INT_RES_NTL_CutOffDays:string, INT_RES_FX_PaymentReset:string, INT_RES_FX_CutOffDays:string, INTEREST_OrgIndex:string, INTEREST_OrgCcy:string, INTEREST_OrgSource:string, OrigMatDate:string, GapDateRoll:string, INT_ACC_UseProj:string, FinNtlExchCal:string, FinCalAtPay:string, Deliverable:string, INT_RES_DorF:string, PayMethod:string, RedemFormula:string, RedemProductName:string, CfRound:string, CfRoundDP:string, INTEREST_Method:string, DENOM_Type:string, DENOM_Amount:string, DENOM_Round:string, DENOM_RoundDP:string, INT_ACC_RateType:string, LoanIntRule:string, LoanBasis:string, LoanRate:string, LoanStubType:string, LoanNtlAmortType:string, PayEvents:string, AdjFstXNL:string, FreqDecompType:string, FreqDecompFreq:string, StubFreqDecompType:string, StubFreqDecompFreq:string, STUB_IgnoreHalfPeriodRule:string, BusMatDate:string, INTEREST_IntRateType:string, ROLL_RolloverStatus:string, SIAsset:string, NotionalDaysGap:string, NotionalGapDRuleID:string, UseIntPayGapRules:string, cOtherAsset:string, cEnv:string, cBack:string, cBreaks:string, ProdData:string, RedemProdData:string -->
 		
	
	<xsl:template match="@*|node()">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
			
    <xsl:template match="ASSET">
<xsl:value-of select="dmAssetId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="dmOwnerTable"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TradeId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SchedEvents"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Type"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SubType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RC"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Book"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PorS"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="EffDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MatDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Notional"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NotExp"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Ccy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_Date1"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_Rate1"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_Date2"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_Rate2"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_StubType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_Decomp"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_CompMode"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_Rounding"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_InterpStyle"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_StartTerm1"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_StartTerm2"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_EndTerm1"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_EndTerm2"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ResetInStub"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_IndexForm"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_YldFreq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_FixFloat"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_Rate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_Basis"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_dmIndex"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_Ccy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_REF_OrgSource"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_REF_Source"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_REF_NNearBy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_Spread"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_Term"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_Type"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_Freq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_Compound"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_AnnDay"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_Rule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_Spread"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_Time"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_CutOffDays"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_CorBGap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_DType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_Round"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_RoundDP"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_Rule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_Time"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_Round"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_RoundDP"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_NTL_REF_OrgSource"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_NTL_REF_Source"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_NTL_REF_NNearBy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_NTL_Cal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_NTL_Term"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FX_REF_OrgSource"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FX_REF_Source"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FX_REF_NNearBy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FX_Cal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FX_Term"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FxDomain"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_ACC_AccCalc"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_ACC_Method"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_ACC_ForL"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_ACC_Basis"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_FwdDecomp"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_DIV_Ratio"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_DIV_ReInvest"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_DIV_DivMethod"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_NRate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_NonContig"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_UnderIndex"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_Freq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_AnnDay"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_Rule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_IntRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_Time"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_Cal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_DRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_Freq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_AnnDay"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_Rule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_IntRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_Time"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_Cal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_DRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="COMP_CompFreq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="COMP_Mode"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Type"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Amount"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Start"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_End"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Freq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Custom"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CROSS_CCY_NotExch"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CROSS_CCY_ForexRate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="OptionData"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PModel"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Style"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="OPForm"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SubIndex"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TermDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ROLL_RollNature"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ROLL_NoticeDays"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ROLL_ReInvestAmt"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="FstCpEffect"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NextFixing"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RiskId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_Conversion"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_RefixMode"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_CutOffMeth"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_Version"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_NtlForm"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_Freq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_AnnDay"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_Rule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_IntRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_Time"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_Cal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_DRuleID"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_FundIndex"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_FundSprd"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MMType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LegRole"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="GroupNum"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NumEve"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Formula"/><xsl:value-of select="$COL_SEP"/>
<xsl:call-template name="deep-copy"><xsl:with-param name="field-name" select = "Events"/></xsl:call-template><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CmdtyQuoteType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_DIV_PayDividend"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_DIV_HedgeDivRatio"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_AuxDomain"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_GapDRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_GapDRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_GapDRuleID"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_DRuleID"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_AVG_Gap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_NTL_Gap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FX_Gap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Pay_Gap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SCHED_Reset_Gap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AMORT_Sched_Gap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_DIV_Gap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ProductName"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AccrualFactors"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_StartCal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_EndCal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_StartSource1"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_StartSource2"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_EndSource1"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_EndSource2"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_InterimRound"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_InterimRoundDP"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_ConvRound"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_ConvRoundDP"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_PaymentReset"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_CutOffDays"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_NTL_PaymentReset"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_NTL_CutOffDays"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FX_PaymentReset"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_FX_CutOffDays"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_OrgIndex"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_OrgCcy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_OrgSource"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="OrigMatDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="GapDateRoll"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_ACC_UseProj"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="FinNtlExchCal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="FinCalAtPay"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Deliverable"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_RES_DorF"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PayMethod"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RedemFormula"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RedemProductName"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CfRound"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CfRoundDP"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_Method"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="DENOM_Type"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="DENOM_Amount"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="DENOM_Round"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="DENOM_RoundDP"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INT_ACC_RateType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LoanIntRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LoanBasis"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LoanRate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LoanStubType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LoanNtlAmortType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PayEvents"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AdjFstXNL"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="FreqDecompType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="FreqDecompFreq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="StubFreqDecompType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="StubFreqDecompFreq"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="STUB_IgnoreHalfPeriodRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BusMatDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="INTEREST_IntRateType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ROLL_RolloverStatus"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SIAsset"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NotionalDaysGap"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NotionalGapDRuleID"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="UseIntPayGapRules"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cOtherAsset"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cEnv"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cBack"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cBreaks"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ProdData"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RedemProdData"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="$ROW_SEP"/>
</xsl:template>
	
	<xsl:template name="deep-copy">
		<xsl:param name="field-name"/>    	
		<xsl:choose>
			<xsl:when test = "./$field-name">
				<xsl:if test="$DEEP_COPY='YES'">	
					<xsl:call-template name="serializeNodeToString">
						<xsl:with-param name="node" select = "./$field-name"/>
					</xsl:call-template>
				</xsl:if>				
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
    </xsl:template>

	<xsl:template name="serializeNodeToString">
		<xsl:param name="node"/>
		<xsl:variable name="name" select="name($node)"/>
		<xsl:if test="$name">
			<xsl:value-of select="concat('&lt;',$name)"/>
			<xsl:for-each select="$node/@*">
				<xsl:value-of select="concat(' ',name(),'=&quot;',.,'&quot; ')"/>
			</xsl:for-each>
			<xsl:value-of select="concat('&gt;',./text())"/>
		</xsl:if>
		<xsl:for-each select="$node/*">
			<xsl:call-template name="serializeNodeToString">
				<xsl:with-param name="node" select="."/>
			</xsl:call-template>
		</xsl:for-each>
		<xsl:if test="$name">
			<xsl:value-of select="concat('&lt;/',$name,'&gt;')"/>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>   

		