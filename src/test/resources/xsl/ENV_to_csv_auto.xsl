<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
<xsl:strip-space elements='*' />
<xsl:param name='COL_SEP' select='"&#09;"' />
<xsl:param name='ROW_SEP' select='"&#10;"' />
<xsl:param name='DEEP_COPY' select='"YES"' />
			<!--HiveHeaders:Audit_Id:string, Audit_Table:string, Audit_Version:string, Audit_Current:string, Audit_EntityState:string, Audit_Authorized:string, Audit_PendingAction:string, Audit_PendingState:string, Audit_IsHistory:string, Audit_User:string, Audit_Action:string, Audit_DateTime:string, Audit_ExtStateName:string, Audit_StateId:string, dmOwnerTable:string, TradeId:string, InputDate:string, InputTime:string, Cust:string, Company:string, BookedBy:string, BookedFor:string, BookSpr:string, BookCcy:string, BookFee:string, BookFeeDate:string, RC:string, Book:string, RCSpread:string, Trader:string, Broker:string, Contact:string, Desk:string, Folder:string, DealId:string, TradeDate:string, TradeStatus:string, RCTrade:string, ProductGroup:string, ProductType:string, BackDated:string, FinalMaturity:string, ProjectId:string, IsMirror:string, PayMethod:string, Comment1:string, Comment2:string, LastUpdate:string, MarketerId:string, NumSSI:string, MirrorSchemeId:string, StructureId:string, ViewName:string, FacilityId:string, TrancheId:string, LoanId:string, LFType:string, LoanCcy:string, ParentOwnerTable:string, ParentTradeId:string, SyndicatedLoan:string, Breakable:string, SsiInstr:string, UltBeneficiary:string, CreditRisk:string, TradeEntryDate:string, SIProductID:string, cBlockNumber:string, cMirrorCust:string -->
 		
	
	<xsl:template match="@*|node()">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
			
    <xsl:template match="ENV">
<xsl:value-of select="Audit_Id"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_Table"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_Version"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_Current"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_EntityState"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_Authorized"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_PendingAction"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_PendingState"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_IsHistory"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_User"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_Action"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_DateTime"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_ExtStateName"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Audit_StateId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="dmOwnerTable"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TradeId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="InputDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="InputTime"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Cust"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Company"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BookedBy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BookedFor"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BookSpr"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BookCcy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BookFee"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BookFeeDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RC"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Book"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RCSpread"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Trader"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Broker"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Contact"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Desk"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Folder"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="DealId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TradeDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TradeStatus"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RCTrade"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ProductGroup"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ProductType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BackDated"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="FinalMaturity"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ProjectId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="IsMirror"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="PayMethod"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Comment1"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Comment2"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LastUpdate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MarketerId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="NumSSI"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="MirrorSchemeId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="StructureId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ViewName"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="FacilityId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TrancheId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LoanId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LFType"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LoanCcy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ParentOwnerTable"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ParentTradeId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SyndicatedLoan"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Breakable"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SsiInstr"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="UltBeneficiary"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CreditRisk"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="TradeEntryDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="SIProductID"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cBlockNumber"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="cMirrorCust"/><xsl:value-of select="$COL_SEP"/>
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

		