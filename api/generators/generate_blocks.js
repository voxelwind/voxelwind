const peData = require('minecraft-data')('pe_0.14'); // todo: update
const fs = require('fs');

const blocks = peData.blocks;
const lines = [];
Object.keys(blocks).forEach(function(blockId) {
    const block = blocks[blockId];
    lines.push("public static final BlockType " + block.name.toUpperCase().replace("'", "") + " = new IntBlock(" +
        [blockId, '"' + block.name + '"', block.stackSize, block.diggable, block.transparent, block.emitLight, block.filterLight,
            "SelfDrop.INSTANCE", "null"].join(", ") + ");");
});

fs.writeFileSync('blocks.java', lines.join('\n'));