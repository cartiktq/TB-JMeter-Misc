<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="US-ASCII" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" />

<xsl:template match="/">
  <html>
    <body>
       <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>

<xsl:template match="testResults">
  <table border="1">
    <tr>
      <th>t</th>
      <th>lt</th>
      <th>rc</th>
      <th>rm</th>
      <th>lb</th>
    </tr>
    <xsl:apply-templates/>
  </table>
</xsl:template>


<xsl:template match="httpSample">
  <tr>
    <td><xsl:value-of select="@t"/></td>
    <td><xsl:value-of select="@lt"/></td>
    <td><xsl:value-of select="@rc"/></td>
    <td><xsl:value-of select="@rm"/></td>
    <td><xsl:value-of select="@lb"/></td>
  </tr>
  <!--<xsl:apply-templates/>-->
</xsl:template>

</xsl:stylesheet>
