//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.22 at 10:25:52 AM MEZ 
//


package org.deegree.services.controller.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         Defines for the security layer the authentication method used for the service.
 *       
 * 
 * <p>Java class for AuthenticationMethodType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthenticationMethodType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HttpBasicAuthentication" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HttpDigestAuthentication" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SOAPAuthentication" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeegreeAuthentication" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationMethodType", propOrder = {
    "httpBasicAuthentication",
    "httpDigestAuthentication",
    "soapAuthentication",
    "deegreeAuthentication"
})
public class AuthenticationMethodType {

    @XmlElement(name = "HttpBasicAuthentication")
    protected String httpBasicAuthentication;
    @XmlElement(name = "HttpDigestAuthentication")
    protected String httpDigestAuthentication;
    @XmlElement(name = "SOAPAuthentication")
    protected String soapAuthentication;
    @XmlElement(name = "DeegreeAuthentication")
    protected String deegreeAuthentication;

    /**
     * Gets the value of the httpBasicAuthentication property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHttpBasicAuthentication() {
        return httpBasicAuthentication;
    }

    /**
     * Sets the value of the httpBasicAuthentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHttpBasicAuthentication(String value) {
        this.httpBasicAuthentication = value;
    }

    /**
     * Gets the value of the httpDigestAuthentication property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHttpDigestAuthentication() {
        return httpDigestAuthentication;
    }

    /**
     * Sets the value of the httpDigestAuthentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHttpDigestAuthentication(String value) {
        this.httpDigestAuthentication = value;
    }

    /**
     * Gets the value of the soapAuthentication property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSOAPAuthentication() {
        return soapAuthentication;
    }

    /**
     * Sets the value of the soapAuthentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSOAPAuthentication(String value) {
        this.soapAuthentication = value;
    }

    /**
     * Gets the value of the deegreeAuthentication property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeegreeAuthentication() {
        return deegreeAuthentication;
    }

    /**
     * Sets the value of the deegreeAuthentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeegreeAuthentication(String value) {
        this.deegreeAuthentication = value;
    }

}
