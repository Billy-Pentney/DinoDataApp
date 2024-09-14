package com.bp.dinodata.data

class TaxonTreeBuilder(
    private val taxonomy: List<String>
){
    companion object {
        const val T_BAR = "┬"
        const val FINAL_CHILD = "└"
        const val FINAL_CHILD_BOLD = "┗"
        const val V_BAR = "│"
        const val V_DOTS = "┆"
    }

    fun getPrintableTree(
        genus: String? = null
    ): List<String> {
        var currentIndent = ""
        val tree = mutableListOf<String>()

        if (taxonomy.isNotEmpty()) {
            tree.add(taxonomy[0])
        }

        val indent = "   "

        for (d in 1..<taxonomy.size) {
            val taxon = taxonomy[d]
            val row = StringBuilder(currentIndent)
            row.append(FINAL_CHILD)
//            if (d == taxonomy.size-1) {
//                row.append(FINAL_CHILD)
//            } else {
//                row.append(CHILD)
//            }
            row.append(" ")
            row.append(taxon)
            currentIndent = "$indent$currentIndent"
            tree.add(row.toString())
        }

        if (genus != null) {
            if (tree.isNotEmpty()) {
                tree.add("$currentIndent $FINAL_CHILD $genus")
            }
            else {
                tree.add(genus)
            }
        }

        return tree
    }
}