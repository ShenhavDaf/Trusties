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
  name: {
    type: String,
    required: true,
  },
  phone: {
    type: String,
    required: true,
  },
  rating: {
    type: Number,
    required: false,
    default: 0,
  },
  verified: {
    type: Boolean,
    default: false,
  },
  tokens: {
    type: [String],
  },
});

module.exports = mongoose.model("User", userSchema);
