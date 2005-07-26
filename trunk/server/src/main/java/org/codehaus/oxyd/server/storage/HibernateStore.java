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

package org.codehaus.oxyd.server.storage;

import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.store.IStore;
import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.document.IBlock;
import org.codehaus.oxyd.kernel.document.DocumentImpl;
import org.hibernate.*;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.impl.SessionImpl;
import org.hibernate.cfg.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HibernateStore implements IStore {
    private final String hibernateConfigurationFile = "/WEB-INF/hibernate.cfg.xml";
    private static final Log log = LogFactory.getLog(HibernateStore.class);
    private SessionFactory sessionFactory;
    private Configuration configuration;

    public void saveDocument(IDocument doc, Context context) throws oxydException {

        log.warn("-------------save Document " + doc.getWorkspace() + "." + doc.getName() + doc.getId());
        try {
            checkHibernate(context);
            beginTransaction(context);
            Session session = getSession(context);
            Query query = session.createQuery("select doc.id from DocumentImpl as doc where doc.id = :id");
            query.setLong("id", doc.getId());
            if (query.uniqueResult()==null)
                session.save(doc);
            else
                session.update(doc);

            Iterator it = doc.getBlocks().values().iterator();
            while (it.hasNext())
                saveBlock((IBlock) it.next(), context);

            endTransaction(context, true);
        }
        catch (Exception e) {
            endTransaction(context, false);
            throw new oxydException(oxydException.MODULE_HIBERNATE_STORE, oxydException.ERROR_UNKNOWN, e.getMessage());
        }
    }

    private void saveBlock(IBlock block, Context context)
    {
        Session session = getSession(context);
        Query query = session.createQuery("select block.id from BlockTextImpl as block where block.id = :id");
        query.setLong("id", block.getBlockId());
        if (query.uniqueResult()==null)
            session.save(block);
        else
            session.update(block);
    }

    public IDocument openDocument(String space, String document, Context context) throws oxydException {

        try {
            IDocument doc = new DocumentImpl(space, document);
            log.warn("--------------open Document " + doc.getWorkspace() + "." + doc.getName() + doc.getId());

            checkHibernate(context);
            beginTransaction(context);
            Session session = getSession(context);
            session.load(doc, new Long(doc.getId()));
            loadBlocks(doc, context);
            endTransaction(context, true);
            return doc;
        }
        catch (Exception e) {
            endTransaction(context, false);
            throw new oxydException(oxydException.MODULE_HIBERNATE_STORE, oxydException.ERROR_UNKNOWN, e.getMessage());
        }
    }

    private void loadBlocks(IDocument doc, Context context)
    {
        Query query = getSession(context).createQuery("from BlockTextImpl as block where block.docId=:docid");
        query.setLong("docid", doc.getId());
        List list = query.list();
        Map blocks = new HashMap();
        for (int i=0;i<list.size();i++) {
            IBlock block = ((IBlock)list.get(i));
            block.setDoc(doc);
            blocks.put(new Long(block.getId()), block);
        }
        doc.setBlocks(blocks);
    }


    private SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Configuration getConfiguration() {
        return configuration;
    }

    private void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

        // Helper Methods
    private void initHibernate() throws HibernateException {
        // Load Configuration and build SessionFactory
        String path = getClass().getResource(hibernateConfigurationFile).getFile();

        setConfiguration((new Configuration()).configure(new File(path)));

        setSessionFactory(getConfiguration().buildSessionFactory());
    }

    private void checkHibernate(Context context) throws HibernateException {

        if (getSessionFactory()==null) {
            initHibernate();
        }
                    /* Check Schema */
        if (getSessionFactory()!=null) {
                updateSchema(context);
            }
    }


    private void closeSession(Session session) throws HibernateException {
        if (session!=null) {
            session.close();
        }
    }


    private Session getSession(Context context) {
         Session session = (Session) context.get("hibsession");
         return session;
     }

     private void setSession(Session session, Context context) {
             context.set("hibsession", session);
     }


     private Transaction getTransaction(Context context) {
         Transaction transaction = (Transaction) context.get("hibtransaction");
         return transaction;
     }

     private void setTransaction(Transaction transaction, Context context) {
         context.set("hibtransaction", transaction);
     }


    private boolean beginTransaction(Context context)  throws HibernateException{
        Session session;
        Transaction transaction;
        if ( log.isDebugEnabled() ) log.debug("Trying to get session from pool");
        session = (SessionImpl)getSessionFactory().openSession();
        if ( log.isDebugEnabled() ) log.debug("Taken session from pool " + session);


        setSession(session, context);

        if ( log.isDebugEnabled() ) log.debug("Trying to open transaction");
        transaction = session.beginTransaction();
        if ( log.isDebugEnabled() ) log.debug("Opened transaction " + transaction);
        setTransaction(transaction, context);
        return true;
    }

   private void endTransaction(Context context, boolean commit)
            throws HibernateException {
        Session session = null;
        try {
            session = getSession(context);
            Transaction transaction = getTransaction(context);
            setSession(null, context);
            setTransaction(null, context);

            if (transaction!=null) {
                if ( log.isDebugEnabled() ) log.debug("Releasing hibernate transaction " + transaction);
                if (commit) {
                    transaction.commit();
                } else {
                    // Don't commit the transaction, can be faster for read-only operations
                    transaction.rollback();
                }
            }
        } finally {
            if (session!=null)
                closeSession(session);
        }
    }


       // Let's synchronize this, to only update one schema at a time
    private synchronized void updateSchema(Context context) throws HibernateException {
        Session session;
        Connection connection;
        DatabaseMetadata meta;
        Statement stmt=null;
        Dialect dialect = Dialect.getDialect(getConfiguration().getProperties());
        boolean bTransaction = true;

        try {
            try {
                bTransaction = beginTransaction(context);
                session = getSession(context);
                connection = session.connection();

                meta = new DatabaseMetadata(connection, dialect);
                stmt = connection.createStatement();
            }
            catch (SQLException sqle) {
                if ( log.isErrorEnabled() ) log.error("Failed updating schema: " + sqle.getMessage());
                throw sqle;
            }

            String[] createSQL = configuration.generateSchemaUpdateScript(dialect, meta);

            try {
                for (int j = 0; j < createSQL.length; j++) {
                    final String sql = createSQL[j];
                    if ( log.isDebugEnabled() ) log.debug("Update Schema sql: " + sql);
                    stmt.executeUpdate(sql);
                }
                connection.commit();
            }
            catch (SQLException e) {
                connection.rollback();
                if ( log.isErrorEnabled() ) log.error("Failed updating schema: " + e.getMessage());
            }
            connection.commit();
        }
        catch (Exception e) {
            if ( log.isErrorEnabled() ) log.error("Failed updating schema: " + e.getMessage());
        }
        finally {

            try {
                if (stmt!=null) stmt.close();
                if (bTransaction)
                    endTransaction(context, true);
            }
            catch (Exception e) {
            }
        }
    }
}
