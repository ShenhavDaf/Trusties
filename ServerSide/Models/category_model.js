const mongoose = require("mongoose");

const categorySchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    default: "me",
  },
  posts: {
    type: [mongoose.Schema.Types.ObjectId],
    default: [],
  },
});

module.exports = mongoose.model("Category", categorySchema);
