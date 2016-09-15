const peData = require('minecraft-data')('pe_0.14'); // todo: update
const fs = require('fs');
const titlize = require('titlize');

const blocks = peData.blocks;
const lines = [];
Object.keys(blocks).forEach(function(blockId) {
    const block = blocks[blockId];
    var name = titlize(block.displayName.replace(/[^a-zA-Z0-9 ]/g, "").replace(/inactive/i, "").trim()).toUpperCase().replace(/ /g, "_");
    if (name.startsWith("BLOCK_OF_")) {
        name = name.substring("BLOCK_OF_".length) + "_BLOCK";
    }

    var line = "public static final BlockType " + name + " = IntBlock.builder()" +
        ".name(\"" + name + "\").id(" + blockId + ").maxStackSize(" + block.stackSize + ").diggable(" + block.diggable + ").transparent(" + block.transparent +
        ").emitLight(" + block.emitLight + ").filterLight(" + block.filterLight + ").build();";
    lines.push(line);
});

fs.writeFileSync('blocks.java', lines.join('\n'));