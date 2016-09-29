const peData = require('minecraft-data')('pe_0.15'); // todo: update
const fs = require('fs');

const items = peData.items;
const lines = [];
Object.keys(items).forEach(function(itemId) {
    const item = items[itemId];
    lines.push("public static final ItemType " + item.name.toUpperCase().replace("'", "") + " = new IntItem(" +
        [itemId, '"' + item.name + '"', item.stackSize, "null"].join(", ") + ");");
});

fs.writeFileSync('items.java', lines.join('\n'));