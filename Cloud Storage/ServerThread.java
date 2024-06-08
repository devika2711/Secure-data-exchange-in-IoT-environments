package com;
public class ServerThread extends Thread
{
	CloudServer server;
public ServerThread(CloudServer server){
	this.server=server;
	start();
}
public void run(){
	server.start();
}
}