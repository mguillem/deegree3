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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         Defines the service for which a sub-controller will be installed by the frontcontroller. The
 *         Configuration location may point to an absolute
 *         file or a relative file, starting from the
 *         service_configuration.xml
 *       
 * 
 * <p>Java class for ServiceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceName" type="{http://www.deegree.org/webservices}AllowedServices"/>
 *         &lt;element name="ConfigurationLocation" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="ControllerClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceType", propOrder = {
    "serviceName",
    "configurationLocation",
    "controllerClass"
})
public class ServiceType {

    @XmlElement(name = "ServiceName", required = true)
    protected AllowedServices serviceName;
    @XmlElement(name = "ConfigurationLocation", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String configurationLocation;
    @XmlElement(name = "ControllerClass")
    protected String controllerClass;

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link AllowedServices }
     *     
     */
    public AllowedServices getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedServices }
     *     
     */
    public void setServiceName(AllowedServices value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the configurationLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfigurationLocation() {
        return configurationLocation;
    }

    /**
     * Sets the value of the configurationLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfigurationLocation(String value) {
        this.configurationLocation = value;
    }

    /**
     * Gets the value of the controllerClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllerClass() {
        return controllerClass;
    }

    /**
     * Sets the value of the controllerClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllerClass(String value) {
        this.controllerClass = value;
    }

}
