
const Post = require('../Models/post_model');
const Sos = require('../Models/sos_model');
const Comment = require('../Models/comment_model');


const addSos = async (req, res, next) => {
    console.log('add new sos: ' + req.body.message);
    sender = req.user.id;
    var message = req.body.message;
    var title = req.body.title;
    var time = req.body.time;
    var role = "SOS";
    var status = req.body.status;



    var freinds_circle = req.body.freinds_circle;
    var area = req.body.area;
    var address = req.body.address;


    const sos = Sos({
        sender: sender,
        title: title,
        time: time,
        message: message,
        role: role,
        status: status,

        freinds_circle: freinds_circle,
        area: area,
        address: address

    });


    sos.save((error, newPost) => {
        if (error) {
            res.status(400).send({
                'status': 'fail',
                'error': error.message
            });
        } else {
            res.status(200).send({
                'status': 'OK',
                'post': newPost
            });
        }
    });
}

const getSoss = async (req, res, next) => {

    Post.find({ role: 'SOS' }, function (err, docs) {
        if (err) {
            console.log(err);
        }
        else {
            res.status(200).send(docs);
        }
    });
};






module.exports = {

    addSos,
    getSoss
};