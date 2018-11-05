
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPConnectionCheck {

    public static void main(String[] args) {

        String username = "";
        String propertiesLocation = "config.properties";
        if (args.length == 1) {
            username = args[0];
        } else {
            System.out.println("Provide username as params");
            System.exit(1);
        }

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(propertiesLocation);
            prop.load(input);
        } catch (IOException e) {
            System.out.println("Error in reading the configuration file.");
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        //get the property value
        String ConnectionURL = prop.getProperty("ConnectionURL");
        String ConnectionName = prop.getProperty("ConnectionName");
        String ConnectionPassword = prop.getProperty("ConnectionPassword");
        String UserSearchBase = prop.getProperty("UserSearchBase");
        String TrustStoreLocation = prop.getProperty("TrustStoreLocation");
        String TrustStorePassword = prop.getProperty("TrustStorePassword");
        Boolean enableDebug = Boolean.valueOf(prop.getProperty("EnableDebug"));

        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("output.log"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);
        System.out.println("============  Test is started  ================");
        System.out.println("== ConnectionURL : " + ConnectionURL);
        System.out.println("== TrustStoreLocation : " + TrustStoreLocation);

        System.setProperty("javax.net.ssl.trustStore", TrustStoreLocation);
        System.setProperty("javax.net.ssl.trustStorePassword", TrustStorePassword);
        if (enableDebug) {
            System.setProperty("javax.net.debug", "all");
        }

        Hashtable<String, String> environment = new Hashtable<String, String>();

        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
//        environment.put(Context.REFERRAL, "follow");
        environment.put(Context.PROVIDER_URL, ConnectionURL);
        environment.put(Context.SECURITY_PRINCIPAL, ConnectionName);
        environment.put(Context.SECURITY_CREDENTIALS, ConnectionPassword);

        DirContext ctx = null;
        NamingEnumeration<SearchResult> results = null;
        try {
            ctx = new InitialDirContext(environment);

            System.out.println("Checking whether the user " + username + "exists in LDAP");
            String searchFilter = "(&(objectClass=person)(uid=" + username + "))";
            //String searchFilter = "(&(objectClass=person)(cn=admin))";
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = ctx.search(UserSearchBase, searchFilter, searchControls);

            if (results.hasMore()) {
                SearchResult searchResult = null;
                try {
                    searchResult = results.next();
                    System.out.println("User exits LDAP : " + searchResult.getNameInNamespace());
                } catch (Exception e) {
                    System.out.println("Error occurred during getting user information");
                    e.printStackTrace();
                }
            } else {
                System.out.println("User " + username + " does not exists in LDAP. So existing");
                System.exit(1);
            }
        } catch (NamingException e) {
            System.out.println("Error occurred when connecting to the LDAP");
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        System.out.println("===========================");

        System.exit(0);
    }
}
