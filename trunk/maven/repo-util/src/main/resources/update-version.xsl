<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes"/>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

    <!--  want no new-lines in this next section, because they would show up in the output file! -->
    <!--  also, double quotes are reduced to single quotes because we use message format to insert versions -->
    <xsl:template match="/*[local-name()=''project'']/*[local-name()=''version'']/text()[contains(.,''{0}'')]">{1}</xsl:template>
    
    <xsl:template match="/*[local-name()=''project'']/*[local-name()=''parent'']/*[local-name()=''version'']/text()[contains(.,''{0}'')]">{1}</xsl:template>

	<!-- search and replace a substring of the SCM URL's (using the values given on the command line) 
    <xsl:template match="*[local-name()=''scm'']/*/text()">
        <xsl:analyze-string select="." regex="http(.*){2}(.*)">
            <xsl:matching-substring>http<xsl:value-of select="regex-group(1)"/>{3}<xsl:value-of select="regex-group(2)"/></xsl:matching-substring>
 			<xsl:non-matching-substring><xsl:value-of select="."/></xsl:non-matching-substring>            
        </xsl:analyze-string>
	</xsl:template>
	-->

	<!-- fill in <connection>, <developerConnection> and <url> under <scm>  -->
    <xsl:template match="/*[local-name()=''project'']/*[local-name()=''scm'']/*">
      <xsl:copy>
      	<xsl:choose>
          <xsl:when test="local-name()=''connection'' or local-name()=''developerConnection''">'{2}'</xsl:when>
          <xsl:when test="local-name()=''url''">'{3}'</xsl:when>
          <xsl:otherwise>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:copy>
	</xsl:template>

	<!-- 
	   <connection> and <developerConnection> text must start with "scm:svn:http"
       If it starts with http, then add scm:svn in front of it
    <xsl:template match="*[local-name()=''connection'']">
    	<connection>hello!</connection>
	</xsl:template>
    -->  
	
</xsl:stylesheet>

