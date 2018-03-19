package com.marklenoury.shaka

import scala.collection.mutable.HashMap
import java.util.UUID

import akka.pattern.ask
import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import com.marklenoury.shaka.messages.{ClientMessage, GetMessages, NodeMessage, RegisterClient}

class ShakaNode() extends Actor {
  val log = Logging(context.system, this)
  log.info(s"${self.path.toString}")
  val clients = HashMap[String, ActorRef]()

  //val shakaNodeNeighbours: Map[uuid, actorSelection]


  private def SendMessage(message: ClientMessage): Unit = {
    val clientActorRef = this.clients.get(message.to)
    if (clientActorRef.isDefined) {
      clientActorRef.get ! message
    }
  }

  private def RegisterClient(username: String): Unit = {
    val clientActorRef = context.system.actorOf(Props(new Client(username)))
    this.clients.put(username, clientActorRef)
  }


  override def receive: Receive = {
    case message: ClientMessage => {
      this.log.info(s"New message from: ${message.from} to: ${message.to}")
      this.SendMessage(message)
    }

    case registerClient: RegisterClient => {
      this.log.info(s"Registering client: ${registerClient.username}")
      this.RegisterClient(registerClient.username)
    }

    case getMessages: GetMessages => {
      val clientActorRefOpt = this.clients.get(getMessages.username)
      if (clientActorRefOpt.isDefined) {
        clientActorRefOpt.get.forward(getMessages)
      }
    }

    case NodeMessage(message) => {
      this.log.info(message)
    }
  }
}

object ShakaNode {

}
