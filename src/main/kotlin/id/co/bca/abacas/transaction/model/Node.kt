class Node {
    var parent: Node? = null
    val child: ArrayList<Node> = ArrayList()
    var value: String? = null

    constructor()

    constructor(value: String, parent: Node){
        this.value = value
        this.parent = parent
    }
}