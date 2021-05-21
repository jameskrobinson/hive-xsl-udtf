<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xpath-default-namespace='http://www.fpml.org/FpML-5/confirmation'>
<xsl:output method='text' encoding='UTF-8'/>
<xsl:strip-space elements='*' />

<xsl:param name='COL_SEP' select='"&#09;"' />
<xsl:param name='ROW_SEP' select='"&#10;"' />

<!--HiveHeaders:payerParty:string, eventDate:string, notional:string -->

<xsl:template match='/dataDocument/trade/swaption/swap/swapStream[*]/cashflows | /dataDocument/trade/swap/swapStream[*]/cashflows'>
<xsl:for-each select='./paymentCalculationPeriod'>
	<xsl:value-of select='../../payerPartyReference/@href'/><xsl:value-of select="$COL_SEP"/>
	<xsl:value-of select='./adjustedPaymentDate'/><xsl:value-of select="$COL_SEP"/>
	<xsl:value-of select='./calculationPeriod/notionalAmount'/><xsl:value-of select="$COL_SEP"/>
	<xsl:value-of select="$ROW_SEP"/>
</xsl:for-each>
</xsl:template>

<xsl:template match="@*|node()">
	<xsl:apply-templates select="@*|node()"/>
</xsl:template>


</xsl:stylesheet>
		