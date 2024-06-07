package server.command;

import server.Response;

public interface Command {

   void execute();
   Response getResult();
   boolean getServerStatus();
}
