const timespan = require("jsonwebtoken/lib/timespan");
const mongoose = require("mongoose");

const postSchema = new mongoose.Schema({
  sender: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
  },
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
  time: {
    type: Date,
    default: Date.now,
  },
  role: {
    type: String,
    enum: ["QUESTION", "SOS"],
    default: "QUESTION",
  },
  status: {
    type: String,
    enum: ["OPEN", "CLOSE", "WAITING"],
    default: "OPEN",
  },
  isDeleted: {
    type: Boolean,
    default: false,
  },
});

module.exports = mongoose.model("Post", postSchema);
