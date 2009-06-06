/* Copyright (c) 2009 Lukas Lalinsky
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the MusicBrainz project nor the names of the
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.musicbrainz.search;

import java.io.*;
import java.math.BigInteger;

import org.apache.lucene.document.Document;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.jthink.brainz.mmd.*;

public class LabelXmlWriter extends XmlWriter {

    public void write(PrintWriter out, Results results) throws IOException {

        try {

            Marshaller m = context.createMarshaller();
            ObjectFactory of = new ObjectFactory();

            Metadata metadata = of.createMetadata();
            LabelList labelList = of.createLabelList();

            for (Result result : results.results) {
                Document doc = result.doc;
                Label label = of.createLabel();
                label.setId(doc.get(LabelIndexField.LABEL_ID.getName()));
                label.setType(StringUtils.capitalize(doc.get(LabelIndexField.TYPE.getName())));

                label.getOtherAttributes().put(new QName("ext:score"), String.valueOf((int) (result.score * 100)));

                String name = doc.get(LabelIndexField.LABEL.getName());
                if (name != null) {
                    label.setName(name);

                }

                String sortname = doc.get(LabelIndexField.SORTNAME.getName());
                if (sortname != null) {
                    label.setSortName(sortname);

                }

                String begin = doc.get(LabelIndexField.BEGIN.getName());
                String end = doc.get(LabelIndexField.END.getName());
                if (begin != null || end != null) {
                    LifeSpan lifespan = of.createLifeSpan();
                    if (begin != null) {
                        lifespan.setBegin(begin);

                    }
                    if (end != null) {
                        lifespan.setEnd(end);

                    }
                    label.setLifeSpan(lifespan);

                }

                String comment = doc.get(LabelIndexField.COMMENT.getName());
                if (comment != null) {
                    label.setDisambiguation(comment);
                }

                labelList.getLabel().add(label);

            }
            labelList.setCount(BigInteger.valueOf(results.results.size()));
            labelList.setOffset(BigInteger.valueOf(results.offset));
            metadata.setLabelList(labelList);
            m.marshal(metadata,out);

        }
        catch (JAXBException je) {
            throw new IOException(je);
        }
    }
}