package com;
public class KeyServerThread extends Thread{
	KeyServer server;
public KeyServerThread(KeyServer server){
	this.server=server;
	start();
}
public void run(){
	server.start();
}
}