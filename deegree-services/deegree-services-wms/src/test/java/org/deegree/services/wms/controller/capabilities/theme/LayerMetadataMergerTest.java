/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -
 and
 - Occam Labs UG (haftungsbeschränkt) -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 Occam Labs UG (haftungsbeschränkt)
 Godesberger Allee 139, 53175 Bonn
 Germany

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.services.wms.controller.capabilities.theme;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.commons.ows.metadata.Description;
import org.deegree.commons.tom.ows.CodeType;
import org.deegree.commons.tom.ows.LanguageString;
import org.deegree.commons.utils.Pair;
import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.GeometryFactory;
import org.deegree.geometry.metadata.SpatialMetadata;
import org.deegree.layer.Layer;
import org.deegree.layer.metadata.LayerMetadata;
import org.deegree.layer.metadata.MetadataUrl;
import org.deegree.theme.Theme;
import org.deegree.theme.persistence.standard.StandardTheme;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link LayerMetadataMerger}.
 *
 * @author <a href="mailto:schneider@occamlabs.de">Markus Schneider</a>
 *
 * @since 3.3
 */
public class LayerMetadataMergerTest {

    private final LayerMetadataMerger merger = new LayerMetadataMerger();

    @Test
    public void mergeEmptyThemeNoLayers() {
        final Theme theme = createThemeWithoutMetadata( "Theme", null, null );
        final LayerMetadata merged = merger.merge( theme );
        assertEquals( "Theme", merged.getName() );
        final Description description = merged.getDescription();
        assertEquals( 0, description.getTitles().size() );
        assertEquals( 0, description.getAbstracts().size() );
        assertEquals( 0, description.getKeywords().size() );
        assertNull( merged.getSpatialMetadata() );
    }

    @Test
    public void mergeSingleThemeNoLayers() {
        final Theme theme = createTheme( "Theme", -180.0, -90.0, 180.0, 90, null, null );
        final LayerMetadata merged = merger.merge( theme );
        assertEquals( "Theme", merged.getName() );
        final Description description = merged.getDescription();
        assertSingleEntry( "Theme Title", description.getTitles() );
        assertSingleEntry( "Theme Abstract", description.getAbstracts() );
        final SpatialMetadata spatialMetadata = merged.getSpatialMetadata();
        assertEquals( "EPSG:4326", spatialMetadata.getCoordinateSystems().get( 0 ).getAlias() );
        assertEquals( -180.0, spatialMetadata.getEnvelope().getMin().get0(), 0.0000001 );
        assertEquals( -90.0, spatialMetadata.getEnvelope().getMin().get1(), 0.0000001 );
        assertEquals( 180.0, spatialMetadata.getEnvelope().getMax().get0(), 0.0000001 );
        assertEquals( 90.0, spatialMetadata.getEnvelope().getMax().get1(), 0.0000001 );
    }

    @Test
    public void mergeEmptyThemeSingleSubtheme() {
        final Theme subTheme = createTheme( "Subtheme", -180.0, -90.0, 180.0, 90, null, null );
        final List<Theme> subThemes = singletonList( subTheme );
        final Theme theme = createThemeWithoutMetadata( "Theme", null, subThemes );
        final LayerMetadata merged = merger.merge( theme );
        assertEquals( "Theme", merged.getName() );
        final Description description = merged.getDescription();
        assertEquals( 0, description.getTitles().size() );
        assertEquals( 0, description.getAbstracts().size() );
        assertEquals( 0, description.getKeywords().size() );
        assertNull( merged.getSpatialMetadata() );
    }

    @Test
    public void mergeEmptyThemeSingleLayer() {
        final Layer layer = createLayer( "Layer", -180.0, -90.0, 180.0, 90 );
        final List<Layer> layers = Collections.singletonList( layer );
        final Theme theme = createThemeWithoutMetadata( "Theme", layers, null );
        final LayerMetadata merged = merger.merge( theme );
        assertEquals( "Theme", merged.getName() );
        final Description description = merged.getDescription();
        assertEquals( 1, description.getTitles().size() );
        assertSingleEntry( "Layer Title", description.getTitles() );
        assertSingleEntry( "Layer Abstract", description.getAbstracts() );
        final SpatialMetadata spatialMetadata = merged.getSpatialMetadata();
        assertEquals( "EPSG:4326", spatialMetadata.getCoordinateSystems().get( 0 ).getAlias() );
        assertEquals( -180.0, spatialMetadata.getEnvelope().getMin().get0(), 0.0000001 );
        assertEquals( -90.0, spatialMetadata.getEnvelope().getMin().get1(), 0.0000001 );
        assertEquals( 180.0, spatialMetadata.getEnvelope().getMax().get0(), 0.0000001 );
        assertEquals( 90.0, spatialMetadata.getEnvelope().getMax().get1(), 0.0000001 );
    }

    @Test
    public void mergeEmptyThemeSingleSubthemeWithLayer() {
        final Layer layer = createLayer( "Layer", -180.0, -90.0, 180.0, 90 );
        final List<Layer> layers = Collections.singletonList( layer );
        final Theme subTheme = createTheme( "Subtheme", -180.0, -90.0, 180.0, 90, layers, null );
        final List<Theme> subThemes = singletonList( subTheme );
        final Theme theme = createThemeWithoutMetadata( "Theme", null, subThemes );
        final LayerMetadata merged = merger.merge( theme );
        assertEquals( "Theme", merged.getName() );
        final Description description = merged.getDescription();
        assertEquals( 1, description.getTitles().size() );
        assertSingleEntry( "Layer Title", description.getTitles() );
        assertSingleEntry( "Layer Abstract", description.getAbstracts() );
        final SpatialMetadata spatialMetadata = merged.getSpatialMetadata();
        assertEquals( "EPSG:4326", spatialMetadata.getCoordinateSystems().get( 0 ).getAlias() );
        assertEquals( -180.0, spatialMetadata.getEnvelope().getMin().get0(), 0.0000001 );
        assertEquals( -90.0, spatialMetadata.getEnvelope().getMin().get1(), 0.0000001 );
        assertEquals( 180.0, spatialMetadata.getEnvelope().getMax().get0(), 0.0000001 );
        assertEquals( 90.0, spatialMetadata.getEnvelope().getMax().get1(), 0.0000001 );
    }

    @Test
    public void mergeThemeWithMetadataUrlsLayers()
                            throws Exception {
        final Theme theme = createThemeWithoutMetadata( "Theme", null, null );
        List<MetadataUrl> metadataUrls = new ArrayList<MetadataUrl>();
        MetadataUrl metadataUrl1 = new MetadataUrl( "format1", "type1", new URL( "http://onlineResource1.de" ) );
        MetadataUrl metadataUrl2 = new MetadataUrl( "format2", "type2", new URL( "http://onlineResource2.de" ) );
        metadataUrls.add( metadataUrl1 );
        metadataUrls.add( metadataUrl2 );
        theme.getLayerMetadata().setMetadataUrls( metadataUrls );

        final LayerMetadata merged = merger.merge( theme );

        List<MetadataUrl> mergedMetadataUrls = merged.getMetadataUrls();
        assertThat( mergedMetadataUrls.size(), is( 2 ) );
        assertThat( mergedMetadataUrls.get( 0 ).getFormat(),
                    either( is( metadataUrl1.getFormat() ) ).or( is( metadataUrl2.getFormat() ) ) );
        assertThat( mergedMetadataUrls.get( 1 ).getFormat(),
                    either( is( metadataUrl1.getFormat() ) ).or( is( metadataUrl2.getFormat() ) ) );
    }

    private Theme createThemeWithoutMetadata( final String name, final List<Layer> layers, final List<Theme> themes ) {
        final LayerMetadata metadata = createEmptyLayerMetadata();
        metadata.setName( name );
        final List<Layer> layersNotNull = new ArrayList<Layer>();
        if ( layers != null ) {
            layersNotNull.addAll( layers );
        }
        final List<Theme> themesNotNull = new ArrayList<Theme>();
        if ( themes != null ) {
            themesNotNull.addAll( themes );
        }
        return new StandardTheme( metadata, themesNotNull, layersNotNull, null );
    }

    private Theme createTheme( final String name, final double minx, final double miny, final double maxx,
                               final double maxy, final List<Layer> layers, final List<Theme> themes ) {
        final LayerMetadata metadata = createLayerMetadata( name, minx, miny, maxx, maxy );
        final List<Layer> layersNotNull = new ArrayList<Layer>();
        if ( layers != null ) {
            layersNotNull.addAll( layers );
        }
        final List<Theme> themesNotNull = new ArrayList<Theme>();
        if ( themes != null ) {
            themesNotNull.addAll( themes );
        }
        return new StandardTheme( metadata, themesNotNull, layersNotNull, null );
    }

    private Layer createLayer( final String name, final double minx, final double miny, final double maxx,
                               final double maxy ) {
        final Layer layer = Mockito.mock( Layer.class );
        final LayerMetadata metadata = createLayerMetadata( name, minx, miny, maxx, maxy );
        when( layer.getMetadata() ).thenReturn( metadata );
        return layer;
    }

    private LayerMetadata createEmptyLayerMetadata() {
        final List<LanguageString> titles = emptyList();
        final List<LanguageString> abstracts = emptyList();
        final List<Pair<List<LanguageString>, CodeType>> keywords = new ArrayList<Pair<List<LanguageString>, CodeType>>();
        final Description description = new Description( null, titles, abstracts, keywords );
        final SpatialMetadata spatialMetadata = null;
        return new LayerMetadata( null, description, spatialMetadata );
    }

    private LayerMetadata createLayerMetadata( final String name, final double minx, final double miny,
                                               final double maxx, final double maxy ) {
        final List<LanguageString> titles = singletonList( new LanguageString( name + " Title", null ) );
        final List<LanguageString> abstracts = singletonList( new LanguageString( name + " Abstract", null ) );
        final List<Pair<List<LanguageString>, CodeType>> keywords = new ArrayList<Pair<List<LanguageString>, CodeType>>();
        final Description description = new Description( name, titles, abstracts, keywords );
        final ICRS crs = CRSManager.getCRSRef( "EPSG:4326" );
        final Envelope envelope = new GeometryFactory().createEnvelope( minx, miny, maxx, maxy, crs );
        final List<ICRS> coordinateSystems = singletonList( crs );
        final SpatialMetadata spatialMetadata = new SpatialMetadata( envelope, coordinateSystems );
        return new LayerMetadata( name, description, spatialMetadata );
    }

    private void assertSingleEntry( final String expected, final List<LanguageString> actual ) {
        assertEquals( 1, actual.size() );
        assertEquals( expected, actual.get( 0 ).getString() );
    }
}
