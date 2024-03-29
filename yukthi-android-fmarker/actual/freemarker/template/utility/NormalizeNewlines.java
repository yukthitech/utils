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
package freemarker.template.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.TemplateTransformModel;

/**
 * <p>Transformer that supports FreeMarker legacy behavior: all newlines appearing
 * within the transformed area will be transformed into the platform's default
 * newline. Unlike the old behavior, however, newlines generated by the data
 * model are also converted. Legacy behavior was to leave newlines in the
 * data model unaltered.</p>
 *
 * <p>Usage:<br>
 * From java:</p>
 * <pre>
 * SimpleHash root = new SimpleHash();
 *
 * root.put( "normalizeNewlines", new freemarker.template.utility.NormalizeNewlines() );
 *
 * ...
 * </pre>
 *
 * <p>From your FreeMarker template:</p>
 * <pre>
 * &lt;transform normalizeNewlines&gt;
 *   &lt;html&gt;
 *   &lt;head&gt;
 *   ...
 *   &lt;p&gt;This template has all newlines normalized to the current platform's
 *   default.&lt;/p&gt;
 *   ...
 *   &lt;/body&gt;
 *   &lt;/html&gt;
 * &lt;/transform&gt;
 * </pre>
 */
public class NormalizeNewlines implements TemplateTransformModel {

    public Writer getWriter(final Writer out,
                            final Map args) {
        final StringBuilder buf = new StringBuilder();
        return new Writer() {
            @Override
            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
            }

            @Override
            public void flush() throws IOException {
                out.flush();
            }

            @Override
            public void close() throws IOException {
                StringReader sr = new StringReader(buf.toString());
                StringWriter sw = new StringWriter();
                transform(sr, sw);
                out.write(sw.toString());
            }
        };
    }

    /**
     * Performs newline normalization on FreeMarker output.
     *
     * @param in the input to be transformed
     * @param out the destination of the transformation
     */
    public void transform(Reader in, Writer out) throws IOException {
        BufferedReader br = (in instanceof BufferedReader)
                            ? (BufferedReader) in
                            : new BufferedReader(in);
        PrintWriter pw = (out instanceof PrintWriter)
                         ? (PrintWriter) out
                         : new PrintWriter(out);
        String line = br.readLine();
        if (line != null) {
            if ( line.length() > 0 ) {
                pw.println(line);
            }
        }
        while ((line = br.readLine()) != null) {
            pw.println(line);
        }
    }
}
