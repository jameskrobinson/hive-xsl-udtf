<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
<xsl:strip-space elements='*' />
<xsl:param name='COL_SEP' select='"&#09;"' />
<xsl:param name='ROW_SEP' select='"&#10;"' />

<!--HiveHeaders:TradeId:string, AssetId:string, Audit_Version:int, Asset_PorS:string, Date:string, ADate:string, LinkEv:string, Type:string, Ccy:string, Amount:string, RiskId:string, EvStyle:string, BDate:string, CDate:string, EventCal:string, EventBDRule:string -->
	
	<xsl:template match="@*|node()">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
			
    <xsl:template match="EVENT">
		<xsl:value-of select="../../TradeId"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="../../dmAssetId"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="../../Audit_Version"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="../../PorS"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="Date"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="ADate"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="LinkEv"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="Type"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="Ccy"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="Amount"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="RiskId"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="EvStyle"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="BDate"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="CDate"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="EventCal"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="EventBDRule"/><xsl:value-of select="$COL_SEP"/>
		<xsl:value-of select="$ROW_SEP"/>
	</xsl:template>

</xsl:stylesheet>   

		