/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
package org.apache.myfaces.examples.outputLinkDynamic;

import org.apache.myfaces.custom.fileupload.UploadedFile;

/**
 * @author Sylvain Vieujot (latest modification by $Author: grantsmith $)
 * @version $Revision: 472792 $ $Date: 2006-11-09 01:34:47 -0500 (Thu, 09 Nov 2006) $
 */
public class OutputLinkDynamicBean
{
    private UploadedFile _upFile;


    public UploadedFile getUpFile()
    {
        return _upFile;
    }

    public void setUpFile(UploadedFile upFile)
    {
    	this._upFile = upFile;
    }

    public boolean isUploaded()
    {
    	return _upFile != null;
    }
    
    public Class getResourceRenderer()
    {
        return UploadedFileRenderer.class;
    }
}