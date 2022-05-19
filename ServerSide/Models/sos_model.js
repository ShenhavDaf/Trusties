const mongoose = require("mongoose");
const Base = require("./post_model");

const sosSchema = Base.discriminator(
  "Sos",
  new mongoose.Schema({
    friends_circle: {
      type: Number,
      required: false,
      default: 2,
    },
    area: {
      type: String,
      // required: true,
      required: false,
    },
    address: {
      type: String,
      // required: true,
      required: false,
    },

    volunteers: {
      type: [mongoose.Schema.Types.ObjectId],
    },
    approved_volunteer: {
      type: mongoose.Schema.Types.ObjectId,
      default: null,
    },
    location: {
      type: String,
      required: false,
      default: "",
    },
  })
);

module.exports = mongoose.model("Sos");
