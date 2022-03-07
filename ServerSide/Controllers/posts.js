
const Post = require('../Models/post_model');
const Sos = require('../Models/sos_model');


const getPosts = async (req, res, next) => {
    try {
        posts = await Post.find()
        res.status(200).send(posts);
    } catch (err) {
        res.status(400).send({
            'status': 'fail',
            'error': error.message
        });
    }
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

    // const post = Post({
    //     message: req.body.message,
    //     sender: sender
    // });

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


module.exports = {
    getPosts,
    getPostsById,
    addPosts,
    addSos
};