/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package freemarker.core;

import java.io.IOException;
import java.io.Writer;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;

/**
 * Represents the XML output format (MIME type "application/xhtml+xml", name "XHTML"). This format escapes by default
 * (via {@link StringUtil#XHTMLEnc(String)}). The {@code ?xml} built-in silently bypasses template output values of the
 * type produced by this output format ({@link TemplateXHTMLOutputModel}).
 * 
 * @since 2.3.24
 */
public final class XHTMLOutputFormat extends CommonMarkupOutputFormat<TemplateXHTMLOutputModel> {

    /**
     * The only instance (singleton) of this {@link OutputFormat}.
     */
    public static final XHTMLOutputFormat INSTANCE = new XHTMLOutputFormat();
    
    private XHTMLOutputFormat() {
        // Only to decrease visibility
    }
    
    @Override
    public String getName() {
        return "XHTML";
    }

    @Override
    public String getMimeType() {
        return "application/xhtml+xml";
    }

    @Override
    public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
        StringUtil.XHTMLEnc(textToEsc, out);
    }

    @Override
    public String escapePlainText(String plainTextContent) {
        return StringUtil.XHTMLEnc(plainTextContent);
    }

    @Override
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("html") || builtInName.equals("xml") || builtInName.equals("xhtml");
    }

    @Override
    protected TemplateXHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateXHTMLOutputModel(plainTextContent, markupContent);
    }

}
