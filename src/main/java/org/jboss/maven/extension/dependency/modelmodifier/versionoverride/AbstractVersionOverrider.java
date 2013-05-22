/**
 * Copyright (C) 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.maven.extension.dependency.modelmodifier.versionoverride;

import java.io.IOException;
import java.util.Map;

import org.apache.maven.model.Model;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.metainf.MetaInfWriter;
import org.jboss.maven.extension.dependency.metainf.generator.OverridePropertiesGenerator;
import org.jboss.maven.extension.dependency.modelmodifier.ModelModifier;
import org.jboss.maven.extension.dependency.util.log.Logging;

/**
 * Abstract class that provides fields and methods common to classes that need to override versions by groupID and
 * artifactID
 */
public abstract class AbstractVersionOverrider
    implements ModelModifier
{
    /**
     * Logging abstraction
     */
    private static final Logger logger = Logging.getLogger();

    protected static Logger getLog()
    {
        return logger;
    }

    /**
     * The character used to separate groupId:arifactId:version
     */
    protected static final String GAV_SEPERATOR = ":";

    /**
     * Writes String/String override maps to .properties format
     * 
     * @param model Model whose build to add the written file to
     * @param overrideName Primary part of the written file name
     * @param overrides Overrides to write as the file content
     */
    protected static void writeOverrideMap( Model model, String overrideName, Map<String, String> overrides )
    {
        try
        {
            MetaInfWriter.writeResource( model, new OverridePropertiesGenerator(overrides, overrideName) );
        }
        catch ( IOException e )
        {
            logger.error( "Could not write " + overrideName + " override map to file due to " + e );
            Logging.logAllCauses( logger, e.getCause() );
        }
    }

}
