/*
 * Reference: snowdeer.github.io & grpc.io
 */
package org.example.service

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.example.proto.Hello
import org.example.proto.HelloServiceGrpc

fun main() {
    println("[client] Starting...")
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 3310)
        .usePlaintext()
        .build()

    var choice: String
    do {
        println("(1) single->single (2) single->multi" +
                " (-1) quit")
        print("Enter the number >> ")
        choice = readLine()!!
        when (Integer.parseInt(choice)) {
            1 -> sayHello(channel) // single->single
            2 -> lotsOfReplies(channel) // single->multi
//            3 -> lotsOfMessages(channel) // multi -> single
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

    print("Enter message >> ")
    val message = readLine()!!
    val author = "client"
    val request = getHelloRequest(message, author)

    val response = stub.sayHello(request)
    println("[client] response() -> (${response?.reply}, ${response?.author}, ${response?.count}")


}

fun lotsOfReplies(channel: ManagedChannel?) {
    val stub = HelloServiceGrpc.newBlockingStub(channel)

    print("Enter the message >> ")
    val message = readLine()!!
    val author = "client"
    print("Enter the number >> ")
    val count = readLine()!!.toInt()
    val request = getHelloRequest(message, author, count)

    val response = stub.lotsOfReplies(request)
    response.forEach {
        println("[client] response() -> (${it.reply}, ${it.author}, ${it.count}")
    }

    println("[client] response.forEach is finished.")

}
