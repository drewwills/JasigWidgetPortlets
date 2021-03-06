/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.springframework.context.support;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySourcesPropertyResolver;


/**
 * @author Josh Helmer, jhelmer@unicon.net
 *
 * Aug 05 2016: This has been included (copied from uPortal) temporarily. The goal
 * for the near future is to have this particular class included as an external
 * dependency once it is split out from uPortal.
 *
 */
@Deprecated
public class ExtendedPropertySourcesPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {
    public static final String EXTENDED_PROPERTIES_SOURCE = "extendedPropertiesSource";

    private PropertyResolver propertyResolver;


    /**
     * Override the postProcessing.  The default PropertySourcesPlaceholderConfigurer does not inject
     * local properties into the Environment object.  It builds a local list of properties files and
     * then uses a transient Resolver to resolve the @Value annotations.   That means that you are
     * unable to get to "local" properties (eg. portal.properties) after bean post-processing has
     * completed unless you are going to re-parse those file.  This is similar to what
     * PropertiesManager does, but it uses all the property files configured, not just portal.properties.
     *
     * If we upgrade to spring 4, there are better/more efficient solutions available.  I'm not aware
     * of better solutions for spring 3.x.
     *
     * @param beanFactory the bean factory
     * @throws BeansException if an error occurs while loading properties or wiring up beans
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (propertyResolver == null) {
            try {
                MutablePropertySources sources = new MutablePropertySources();
                PropertySource<?> localPropertySource = new PropertiesPropertySource(EXTENDED_PROPERTIES_SOURCE, mergeProperties());
                sources.addLast(localPropertySource);

                propertyResolver = new PropertySourcesPropertyResolver(sources);

            } catch (IOException e) {
                throw new BeanInitializationException("Could not load properties", e);
            }
        }

        super.postProcessBeanFactory(beanFactory);
    }


    /**
     * Get a property resolver that can read local properties.
     *
     * @return a property resolver that can be used to dynamically read the merged property
     * values configured in applicationContext.xml
     */
    public PropertyResolver getPropertyResolver() {
        return propertyResolver;
    }
}
