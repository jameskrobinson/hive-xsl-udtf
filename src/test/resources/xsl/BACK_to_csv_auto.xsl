<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
<xsl:strip-space elements='*' />
<xsl:param name='COL_SEP' select='"&#09;"' />
<xsl:param name='ROW_SEP' select='"&#10;"' />
<xsl:param name='DEEP_COPY' select='"YES"' />
			<!--HiveHeaders:dmOwnerTable:string, TradeId:string, ExtTradeId:string, SummitId:string, OldId:string, Code:string, PendingUser:string, PendingDate:string, PendingTime:string, DoneUser:string, DoneDate:string, DoneTime:string, VerifyUser:string, VerifyDate:string, VerifyTime:string, CancelUser:string, CancelDate:string, CancelTime:string, AmendUser:string, AmendDate:string, AmendTime:string, ConfirmExec:string, CollateralHeld:string, CollateralAmt:string, TermTradeDate:string, TermEffDate:string, TermInputDate:string, OrigMatDate:string, OrigNotional:string, Street:string, NetSettle:string, TermAssignStatus:string, SchemaId:string, TrAcctType:string, CommonRef:string, AssignTo:string, AssignFrom:string, Audit_Version:string, TradeNetting:string, ThresholdCcy:string, ThresholdAmount:string, ExerciseTo:string, ExerciseOwnerTable:string, SecId:string, SecType:string, SecCcy:string, MergeTradeId:string, EuroConversionDate:string, OurSettlementCountry:string, CustSettlementCountry:string, PositionType:string, NetSettlement:string, NetCredit:string, MAFormat:string, MAStyle:string, ExternalUser:string, ExtSystem:string, SSIOpsCode:string, ContOpsCode:string, NavOpsCode:string, AggressorFlag:string, MatchingId:string, OrigEffectDate:string, ExtVersion:string, SsiByEvent:string, CLSSettlement:string, CSD:string, CSD_AuditId:string, cPremAcMeth:string, cIntTrade:string, cBusCat:string, MirrorData:string -->
 		
	
	<xsl:template match="@*|node()">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
			
    <xsl:template match="BACK">
<xsl:value-of select="dmOwnerTable"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TradeId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ExtTradeId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SummitId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="OldId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Code"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PendingUser"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PendingDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PendingTime"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="DoneUser"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="DoneDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="DoneTime"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="VerifyUser"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="VerifyDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="VerifyTime"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CancelUser"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CancelDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CancelTime"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AmendUser"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AmendDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AmendTime"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ConfirmExec"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CollateralHeld"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CollateralAmt"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TermTradeDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TermEffDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TermInputDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="OrigMatDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="OrigNotional"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Street"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NetSettle"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TermAssignStatus"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SchemaId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TrAcctType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CommonRef"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AssignTo"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AssignFrom"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_Version"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TradeNetting"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ThresholdCcy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ThresholdAmount"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ExerciseTo"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ExerciseOwnerTable"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SecId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SecType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SecCcy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MergeTradeId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="EuroConversionDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="OurSettlementCountry"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CustSettlementCountry"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PositionType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NetSettlement"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NetCredit"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MAFormat"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MAStyle"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ExternalUser"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ExtSystem"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SSIOpsCode"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ContOpsCode"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NavOpsCode"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="AggressorFlag"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MatchingId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="OrigEffectDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ExtVersion"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SsiByEvent"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CLSSettlement"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CSD"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CSD_AuditId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cPremAcMeth"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cIntTrade"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cBusCat"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MirrorData"/><xsl:value-of select="$COL_SEP"/>
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

		