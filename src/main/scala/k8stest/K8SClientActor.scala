package k8stest

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import io.kubernetes.client.Configuration
import io.kubernetes.client.apis.{BatchV1Api, CoreV1Api}
import io.kubernetes.client.models.V1DeleteOptions
import io.kubernetes.client.util.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class K8SClientActor(managerActor: ActorRef) extends Actor {

  override def preStart(): Unit = {
    managerActor ! "Hello"
  }

  override def receive: Receive = {
    case Exit => context.system.terminate
    case x => println(x)
  }

}

object K8SClientActor extends App {

  println("Starting client...")

  val config = Config.fromCluster            // Running from inside the cluster
  Configuration.setDefaultApiClient(config)  // Set it as default

  val coreApi = new CoreV1Api()
  val batchApi = new BatchV1Api()

  val managerService = coreApi.readNamespacedService("manager-actor", "k8stest", "true", null, null)

  val managerIP = managerService.getSpec.getClusterIP
  val managerPort = managerService.getSpec.getPorts.get(0).getPort

  val myService = coreApi.readNamespacedService("client-actor", "k8stest", "true", null, null)

  val myIp = myService.getSpec.getClusterIP
  val myPort = myService.getSpec.getPorts.get(0).getPort

  println("Gathered the following details:", managerIP, managerPort, myIp, myPort)

  val system = ActorSystem(
    "K8STestActorSystem",
    ConfigFactory.load.withValue(
      "akka.remote.netty.tcp.bind-hostname",
      ConfigValueFactory.fromAnyRef("0.0.0.0")
    ).withValue(
      "akka.remote.netty.tcp.bind-port",
      ConfigValueFactory.fromAnyRef(6000)
    ).withValue(
      "akka.remote.netty.tcp.hostname",
      ConfigValueFactory.fromAnyRef(myIp)
    ).withValue(
      "akka.remote.netty.tcp.port",
      ConfigValueFactory.fromAnyRef(myPort)
    )
  )

  implicit val ex: ExecutionContext = system.dispatcher

  system.actorSelection(
    s"akka.tcp://K8STestActorSystem@$managerIP:$managerPort/user/K8STestManagerActor"
  ).resolveOne(5 seconds).onComplete {
    case Success(managerActor) => system.actorOf(Props(classOf[K8SClientActor], managerActor), "K8STestClientActor")
    case Failure(e) => e.printStackTrace()
  }

  system.whenTerminated.onComplete {_ =>
    coreApi.deleteNamespacedService(
      "client-actor",
      "k8stest",
      new V1DeleteOptions()
        .apiVersion("v1")
        .kind("DeleteOptions")
        .gracePeriodSeconds(0.toLong)
        .propagationPolicy("Background")
      ,
      "true",
      null,
      null,
      null
    )
  }

}