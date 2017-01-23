package vidya.hbase.utils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hughj on 12/10/16.
 */
class NT {
   Pattern subj = Pattern.compile("<([^>]+)>.+");
   Pattern pred = Pattern.compile("<[^>]+>[^<]*<([^>]+)>.+");
   Pattern obj = Pattern.compile("<[^>]+>[^<]*<[^>]+>(.+)\\.");

   private String data;
   private NTObject object;

   public NT(String data) {
      this.data = data;
      this.object = new NTObject();
   }

   public String getSubject() {
      Matcher m = subj.matcher(data);
      if (!m.matches()) {
         throw new IllegalArgumentException("unable to parse SUBJECT expression: " + data);
      }
      return m.group(1);
   }

   public String getPredicate() {
      Matcher m = pred.matcher(data);
      if (!m.matches()) {
         throw new IllegalArgumentException("unable to parse PREDICATE expression: " + data);
      }
      return m.group(1);
   }

   public NTObject getObject() {
      return object;
   }

   public enum ObjectType {
      LITERAL,
      TYPED_LITERAL,
      URI,
      UNKNOWN
   }

   public class NTObject implements Serializable{
      private String body;
      private ObjectType type;

      public NTObject() {
         unapply();
      }

      public ObjectType getType() {
         return type;
      }

      public String getBody() {
         return body;
      }

      void unapply() {
         Matcher m = obj.matcher(data);
         if (!m.matches()) {
            throw new IllegalArgumentException("unable to parse OBJECT expression: " + data);
         }
         String framed = m.group(1).trim();
         if (framed.matches("\".*\"")) {  // its a literal
            body = framed.substring(1, framed.lastIndexOf('"'));
            type = ObjectType.LITERAL;
         } else if (framed.matches("<.*>")) { // its a URI
            body = framed.substring(1, framed.lastIndexOf('>'));
            type = ObjectType.URI;
         } else {
            body = framed;
            type = ObjectType.UNKNOWN;
         }
      }
   }

}
