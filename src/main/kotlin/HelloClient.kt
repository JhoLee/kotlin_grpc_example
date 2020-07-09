/*
 * Reference: snowdeer.github.io & grpc.io
 */
package org.example.service

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import org.example.proto.Hello
import org.example.proto.HelloServiceGrpc
import java.lang.Thread.sleep

fun main() {
    println("[client] Starting...")
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 3310)
        .usePlaintext()
        .build()

    var choice: String
    do {
        println(
            "(1) single->single (2) single->multi (3) multi->single (-1) quit"
        )
        print("Enter the number >> ")
        choice = readLine()!!
        when (Integer.parseInt(choice)) {
            1 -> sayHello(channel) // single->single
            2 -> lotsOfReplies(channel) // single->multi
            3 -> lotsOfGreetings(channel) // multi -> single
//            4 -> bidiHello(channel) // multi -> multi
            -1 -> println("bye.")
            else -> println("???")
        }
    } while (Integer.parseInt(choice) != -1)

}


fun getHelloRequest(message: String, author: String, count: Int = 1): Hello.HelloRequest {
    return Hello.HelloRequest.newBuilder()
        .setMessage(message)
        .setAuthor(author)
        .setCount(count)
        .build()
}

fun sayHello(channel: ManagedChannel) {
    val stub = HelloServiceGrpc.newBlockingStub(channel)

    val message = "exampleMessage"
    val author = "client"
    println("[client] Calling server.sayHello($message, $author)")
    val request = getHelloRequest(message, author)

    val response = stub.sayHello(request)
    println("[client] response -> (${response?.reply}, ${response?.author}, ${response?.count}")


}

fun lotsOfReplies(channel: ManagedChannel?) {
    val stub = HelloServiceGrpc.newBlockingStub(channel)

    val message = "exampleMessage"
    val author = "client"
    val count = 3
    println("[client] Calling server.lotsOfReplies($message, $author, $count)")
    val request = getHelloRequest(message, author, count)

    val response = stub.lotsOfReplies(request)
    response.forEach {
        println("[client] response() -> (${it.reply}, ${it.author}, ${it.count}")
    }

    println("[client] response.forEach is finished.")

}

fun lotsOfGreetings(channel: ManagedChannel?) {
    val asyncStub = HelloServiceGrpc.newStub(channel)
    val requestObserver = asyncStub.lotsOfGreetings(ResponseStreamObserver())

    val message = "exampleMessage"
    val author = "client3"
    val count = 4

    for (i in 1..count) {
        val request = getHelloRequest(message, author, i)
        println("[client] Calling server.lotsOfGreetings($message, $author, $i)")
        requestObserver.onNext(request)
        sleep(1000)
    }
    requestObserver.onCompleted()


}

class ResponseStreamObserver : StreamObserver<Hello.HelloResponse> {
    override fun onNext(value: Hello.HelloResponse?) {
        println("[client] onNext(${value?.reply}, ${value?.author}, ${value?.count})")
    }

    override fun onError(t: Throwable?) {
        println("[client] onError()")
    }

    override fun onCompleted() {
        println("[client] onCompleted()")
    }
}