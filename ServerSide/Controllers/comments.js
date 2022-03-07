
const Post = require('../Models/post_model');
const Sos = require('../Models/sos_model');
const Comment = require('../Models/comment_model');


const addComment = async (req, res, next) => {
    console.log('add new Ques: ' + req.params.id);
    sender = req.user.id;
    // const post_url = req.url;
    // const post_id = String(post_url).split("/");

    post = await Post.findById(req.params.id)

    var message = req.body.message;
    var time = req.body.time;


    const comment = Comment({
        sender: sender,
        post: post,
        time: time,
        message: message,
    });

    comment.save((error, newPost) => {
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

const getAllComments_Post = async (req, res, next) => {

    // try {
    //     posts = await Post.where(p => p.role == "QUES").find()
    //     res.status(200).send(posts);
    // } catch (err) {
    //     res.status(400).send({
    //         'status': 'fail',
    //         'error': error.message
    //     });
    // }

}

module.exports = {

    addComment,
    getAllComments_Post
};