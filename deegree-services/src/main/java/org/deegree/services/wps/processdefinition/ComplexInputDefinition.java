//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.09 at 02:05:18 PM MEZ 
//


package org.deegree.services.wps.processdefinition;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Description of a complex input parameter (XML or binary) to the process.
 * 
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.deegree.org/services/wps}ProcessInputType">
 *       &lt;sequence>
 *         &lt;element name="DefaultFormat" type="{http://www.deegree.org/services/wps}ComplexFormatType"/>
 *         &lt;element name="OtherFormats" type="{http://www.deegree.org/services/wps}ComplexFormatType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="maximumMegabytes" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "defaultFormat",
    "otherFormats"
})
public class ComplexInputDefinition
    extends ProcessletInputDefinition
{

    @XmlElement(name = "DefaultFormat", required = true)
    protected ComplexFormatType defaultFormat;
    @XmlElement(name = "OtherFormats")
    protected List<ComplexFormatType> otherFormats;
    @XmlAttribute
    protected BigInteger maximumMegabytes;

    /**
     * Gets the value of the defaultFormat property.
     * 
     * @return
     *     possible object is
     *     {@link ComplexFormatType }
     *     
     */
    public ComplexFormatType getDefaultFormat() {
        return defaultFormat;
    }

    /**
     * Sets the value of the defaultFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComplexFormatType }
     *     
     */
    public void setDefaultFormat(ComplexFormatType value) {
        this.defaultFormat = value;
    }

    /**
     * Gets the value of the otherFormats property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherFormats property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherFormats().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ComplexFormatType }
     * 
     * 
     */
    public List<ComplexFormatType> getOtherFormats() {
        if (otherFormats == null) {
            otherFormats = new ArrayList<ComplexFormatType>();
        }
        return this.otherFormats;
    }

    /**
     * Gets the value of the maximumMegabytes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumMegabytes() {
        return maximumMegabytes;
    }

    /**
     * Sets the value of the maximumMegabytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumMegabytes(BigInteger value) {
        this.maximumMegabytes = value;
    }

}
