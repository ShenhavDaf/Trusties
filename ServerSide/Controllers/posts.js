
const Post = require('../Models/post_model');
const Sos = require('../Models/sos_model');
const Comment = require('../Models/comment_model');



const getPosts = async (req, res, next) => {

    Post.find({ role: 'QUES' }, function (err, docs) {
        if (err) {
            console.log(err);
        }
        else {
            res.status(200).send(docs);
        }
    });

};

const getPostsById = async (req, res, next) => {
    try {
        posts = await Post.findById(req.params.id)
        res.status(200).send(posts);
    } catch (err) {
        res.status(400).send({
            'status': 'fail',
            'error': err.message
        });
    }
};

const addPosts = async (req, res, next) => {
    console.log('add new post: ' + req.body.message);
    sender = req.user.id;
    var message = req.body.message;
    var title = req.body.title;
    var time = req.body.time;

    var role = req.body.role;
    var status = req.body.status;

    const post = Post({

        sender: sender,
        title: title,
        time: time,
        message: message,
        role: role,
        status: status
    });

    // const post = Post({
    //     message: req.body.message,
    //     sender: sender
    // });

    post.save((error, newPost) => {
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

const getMyPosts = async (req, res, next) => {

    // User.find({ id: req.user.id }, function (err, docs) {
    //     if (err) {
    //         console.log(err);
    //     }
    //     else {
    //         Post.find({ sender: docs }, function (err, docs_post) {
    //             if (err) {
    //                 console.log(err);
    //             }
    //             else {
    //                 res.status(200).send(docs_post);
    //             }
    //         });
    //     }
    // });


};




module.exports = {
    getPosts,
    getPostsById,
    addPosts,
    getMyPosts
};