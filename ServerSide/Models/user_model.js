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
  numberReviews: {
    type: Number,
    required: false,
    default: 0,
  },
  friends: {
    type: [mongoose.Schema.Types.ObjectId],
  },
  waitingList: {
    type: [mongoose.Schema.Types.ObjectId],
  },
  firebaseToken: {
    type: String,
    required: false,
  },
  isSignedIn: {
    type: Boolean,
    default: false,
  },
  notificationsNumber: {
    type: Number,
    required: false,
    default: 0,
  },
});

module.exports = mongoose.model("User", userSchema);
