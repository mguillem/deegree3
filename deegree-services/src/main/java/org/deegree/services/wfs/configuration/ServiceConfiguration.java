//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.09 at 10:40:27 AM MEZ 
//


package org.deegree.services.wfs.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.deegree.commons.datasource.configuration.DirectSQLDataSourceType;
import org.deegree.commons.datasource.configuration.FeatureStoreType;
import org.deegree.commons.datasource.configuration.MemoryFeatureStoreType;
import org.deegree.commons.datasource.configuration.PostGISFeatureStoreType;
import org.deegree.commons.datasource.configuration.ShapefileDataSourceType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.deegree.org/datasource}FeatureStore" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "featureStore"
})
@XmlRootElement(name = "ServiceConfiguration")
public class ServiceConfiguration {

    @XmlElementRef(name = "FeatureStore", namespace = "http://www.deegree.org/datasource", type = JAXBElement.class)
    protected List<JAXBElement<? extends FeatureStoreType>> featureStore;

    /**
     * Gets the value of the featureStore property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the featureStore property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeatureStore().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link FeatureStoreType }{@code >}
     * {@link JAXBElement }{@code <}{@link DirectSQLDataSourceType }{@code >}
     * {@link JAXBElement }{@code <}{@link MemoryFeatureStoreType }{@code >}
     * {@link JAXBElement }{@code <}{@link PostGISFeatureStoreType }{@code >}
     * {@link JAXBElement }{@code <}{@link ShapefileDataSourceType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends FeatureStoreType>> getFeatureStore() {
        if (featureStore == null) {
            featureStore = new ArrayList<JAXBElement<? extends FeatureStoreType>>();
        }
        return this.featureStore;
    }

}
