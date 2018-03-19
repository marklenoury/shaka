package com.marklenoury.shaka

import scala.collection.mutable.MutableList
import akka.actor.Actor
import com.marklenoury.shaka.messages.{ClientMessage, GetMessages}

class Client(username: String) extends Actor {

  val messages = MutableList[ClientMessage]()


  private def RecieveMessage(message: ClientMessage) = {
    this.messages += message
  }

  private def GetMessages(): Array[ClientMessage] = {
    val messages = this.messages.toArray
    this.messages.clear()
    messages
  }

  override def receive: Receive = {
    case message: ClientMessage => {
      this.RecieveMessage(message)
    }

    case get: GetMessages => {
      sender() ! this.GetMessages()
    }
  }
}
