const mongoose = require('mongoose');
const Base = require('./post_model');

const quesSchema = Base.discriminator("Ques", new mongoose.Schema({
    comment: {
        type: String,
        required: true
    },

}),
);

module.exports = mongoose.model('Ques');