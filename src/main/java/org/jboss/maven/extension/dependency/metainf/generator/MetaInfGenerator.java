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
package org.jboss.maven.extension.dependency.metainf.generator;

import java.io.IOException;

import org.apache.maven.model.Model;

public interface MetaInfGenerator
{

    /**
     * Produce the file content as a single String.
     * 
     * @param model Model to get information from
     * @return String of content
     * @throws IOException If there is a problem encountered when generating the content
     */
    public String generateContent( Model model )
        throws IOException;

    /**
     * Get a String that describes the content generated.
     * 
     * @return Short descriptive String of the content that is produced by generateContent()
     */
    public String getDescription();

    /**
     * If the data is going to be written to a file, what extension should it have ideally?
     * 
     * @return a few character String of a file extension to use if writing the data to a file.
     */
    public String getDesiredFileExtension();

}
