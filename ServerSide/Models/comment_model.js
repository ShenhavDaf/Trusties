const timespan = require("jsonwebtoken/lib/timespan");
const mongoose = require("mongoose");

const commentSchema = new mongoose.Schema({
  sender: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
  },
  post: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Post",
  },
  message: {
    type: String,
    required: true,
  },
  time: {
    type: Date,
    default: Date.now,
  },
  isCorrect: {
    type: Boolean,
    default: false,
  },
  isDeleted: {
    type: Boolean,
    default: false,
  },
  report_positive: {
    type: [mongoose.Schema.Types.ObjectId],
    ref: "User",
  },
  report_negative: {
    type: [mongoose.Schema.Types.ObjectId],
    ref: "User",
  },

});

module.exports = mongoose.model("Comment", commentSchema);
