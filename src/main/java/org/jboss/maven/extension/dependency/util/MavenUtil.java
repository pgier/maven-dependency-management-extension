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
package org.jboss.maven.extension.dependency.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Basic implementation of org.sonatype.aether.artifact.Artifact
 */
public class MavenUtil
{

    /**
     * Regex pattern for parsing a Maven GAV
     */
    public static final Pattern gavPattern = Pattern.compile( "\\s*([\\w\\-_.]+):([\\w\\-_.]+):(\\d[\\w\\-_.]+)\\s*" );

    public static boolean validGav(String gav)
    {
        Matcher matcher = gavPattern.matcher( gav );
        return matcher.matches();
    }

}
