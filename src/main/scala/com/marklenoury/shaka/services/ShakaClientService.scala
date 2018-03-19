package com.marklenoury.shaka.services

import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.util.Timeout
import com.marklenoury.shaka.messages.{ClientMessage, GetMessages, RegisterClient}

import scala.concurrent.duration._

class ShakaClientService(shakaNodeAddress: String, system: ActorSystem, username: String) {
  private implicit val timeout = Timeout(2 seconds)

  private val shakaNodeSelection = system.actorSelection(s"akka.tcp://Shaka@$shakaNodeAddress/user/shaka-node")
  // akka.tcp://Shaka@127.0.0.1:2552/user/shaka-node
  //private val shakaNodeSelection = system.actorSelection(s"akka.tcp://Shaka@127.0.0.1:2552/user/shaka-node")

  def run(): Unit = {
    println(s"Running client input loop. q to quit.")
    var running = true
    while (running) {
      val input = scala.io.StdIn.readLine()
      println(s"Got input $input")
      input match {
        case line if line == "q" => {
          println("Got quit command - stopping client")
          running = false
        }

        case line if line.startsWith("send") => {
          //send hello@mark
          val splitter = line.stripPrefix("send").split("@")
          shakaNodeSelection ! ClientMessage(username, splitter(0), splitter(1))
        }

        case line if line.startsWith("get") => {
          val newMessagesFuture = shakaNodeSelection ? GetMessages(username)
          val result = Await.result(newMessagesFuture, timeout.duration).asInstanceOf[Array[ClientMessage]]
          result.map(f => { println(s"${f.from} says ${f.content}")})
        }

        case line if line.startsWith("register") => {
          shakaNodeSelection ! RegisterClient(username)
        }

        case _ => {
            println("Unknown input")
        }
      }
    }

    println("Client stopped")
  }
}
