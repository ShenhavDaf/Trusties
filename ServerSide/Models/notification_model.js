const timespan = require("jsonwebtoken/lib/timespan");
const mongoose = require("mongoose");

const notificationSchema = new mongoose.Schema({
  sender: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
  },
  post: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Post",
  },
  time: {
    type: Date,
    default: Date.now,
  },
  type: {
    type: String,
    required: true,
  },
  circle: {
    type: String,
    required: true,
  },
});

module.exports = mongoose.model("Notification", notificationSchema);
