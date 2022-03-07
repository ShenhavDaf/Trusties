const mongoose = require('mongoose');
const Base = require('./post_model');

const sosSchema = Base.discriminator("Sos", new mongoose.Schema({
    freinds_circle: {
        type: Number,
        required: false,
        default: 2
    },
    area: {
        type: String,
        required: true
    },
    address: {
        type: String,
        required: true
    }

}),
);

module.exports = mongoose.model('Sos');