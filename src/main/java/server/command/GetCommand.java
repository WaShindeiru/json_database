package server.command;

import client.Request;
import server.DatabaseFile;
import server.Response;

public class GetCommand  {

   private Request request;
   private DatabaseFile database;
   private Response response;

   public GetCommand(Request request, DatabaseFile database) {
      this.request = request;
      this.database = database;
   }

}
