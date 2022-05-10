const { ObjectId } = require("mongodb");
const mongoose = require("mongoose");

const userSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true,
  },
  password: {
    type: String,
    required: true,
  },
  tokens: {
    type: [String],
  },
  name: {
    type: String,
    required: true,
  },
  phone: {
    type: String,
    required: true,
  },
  photo: {
    type: String,
    required: false,
  },
  verified: {
    type: Boolean,
    default: false,
  },
  rating: {
    type: Number,
    required: false,
    default: 0,
  },
  friends: {
    type: [mongoose.Schema.Types.ObjectId],
  },
});

module.exports = mongoose.model("User", userSchema);
