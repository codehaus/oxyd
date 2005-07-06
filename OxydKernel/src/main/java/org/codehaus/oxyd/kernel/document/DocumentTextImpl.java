/* ====================================================================
 *   Copyright 2005 Jérémi Joslin.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */
package org.codehaus.oxyd.kernel.document;

import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Utils;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.util.*;
import java.io.StringReader;


public class    DocumentTextImpl extends DocumentImpl {

    public DocumentTextImpl(String name)
    {
        super(name);
    }

    public DocumentTextImpl(String space, String name)
    {
        super(space, name);
    }

    public DocumentTextImpl() {
        super();
    }


    public IBlock createBlock(String pos, byte[] content, Context context)
    {
        IBlock block = new BlockTextImpl();
        block.setContent(content);
        block.setRemoved(false);
        block.setId(super.nextId++);
        block.setPosition(new Long(getBlocks().size() + 1).toString());
        getBlocks().put(new Long(block.getId()), block);
        moveBlock(block.getId(), pos, context);
        return block;
    }

    public void moveBlock(long blockId, String pos, Context context)
    {
        IBlock block  = (IBlock) getBlocks().get(new Long(blockId));
        long BlockPos = new Long(block.getPosition()).longValue();
        long  lPos = new Long(pos).longValue();

        long  moveversion = getNextVersion();

        if (lPos > getBlocks().size())
            lPos = getBlocks().size();
        if (lPos < 1)
            lPos = 1;
        Object[]  blockCol = getBlocks().values().toArray();
        for(int i = 0; i < blockCol.length; i++)
        {
            IBlock tmpBlock = ((IBlock) blockCol[i]);
            int tmpblockPos = (new Long(tmpBlock.getPosition())).intValue();
            if (!tmpBlock.isRemoved() && tmpblockPos >= lPos && lPos < BlockPos)
            {
                tmpBlock.setPosition(new Long(tmpblockPos + 1).toString());
                tmpBlock.setVersion(moveversion);
            }
        }

        block.setPosition(pos);
        block.setVersion(moveversion);
    }



    public void fromXML(String xml) throws oxydException {
        SAXReader reader = new SAXReader();
        Document domdoc = null;

        StringReader in = new StringReader(xml);
        try {
            domdoc = reader.read(in);
        } catch (DocumentException e) {
            throw new oxydException(oxydException.MODULE_DOCUMENT_TEXT_IMPL, oxydException.ERROR_XML_ERROR, "Could not read the XML file");
        }

        Element docEl = domdoc.getRootElement();
        Element infosEl = docEl;//docEl.element("oxyddocument");

        setName(Utils.getElementText(infosEl, "name"));
        setWorkspace(Utils.getElementText(infosEl, "workspace"));
        setDirectory(Utils.getElementText(infosEl, "directory"));
        setVersion(new Long(Utils.getElementText(infosEl, "version")).longValue());
        setParentName(Utils.getElementText(infosEl, "parentname"));

        Element blocksEl = infosEl.element("blocks");

        List ListFile =  blocksEl.elements("block");
        for (int i = 0; i < ListFile.size(); i++)
        {
            Element blockEl = ((Element)ListFile.get(i));
            IBlock block = new BlockTextImpl();
            block.fromXML(blockEl);
            this.getBlocks().put(new Long(block.getId()), block);
        }


    }


}
