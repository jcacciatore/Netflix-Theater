///////////////////////////////////////////////////////////////////////////////
// Netflix Virtual Theater
//
// http://www.netflix.com/WiPlayer?movieid=60035933&trkid=1537777&theater-id=0&user-id=jason
//
if (!NetflixTheater) {
    var NetflixTheater = {};
}    
if (!NetflixTheater.Application) {
    NetflixTheater.Application = {};
}

NetflixTheater._Application = new Class({
    _userId: '',

    _theaterId: '',

    _inputHandler: null,

    _createMsgContainer: null,

    _msgBox: null,

    _conn: null,

    initialize: function() {
	console.log("NetflixTheater - initializing");
	var qparams = window.location.search.slice(1).split('&');
	var i;
	for (i=0; i < qparams.length; i++) {
	    if (qparams[i].indexOf(NetflixTheater.QueryParams.UserId) > -1) {
		this._userId = qparams[i].slice(NetflixTheater.QueryParams.UserId.length + 1);
	    } else if (qparams[i].indexOf(NetflixTheater.QueryParams.TheaterId > -1)) {
		this._theaterId = qparams[i].slice(NetflixTheater.QueryParams.TheaterId.length + 1);
	    }
	}
	
	console.log("initializing ws connection");
        _conn = new WebSocket("ws://hydrogen.slicehost.unclehulka.com:7001/");
        _conn.onmessage = function(evt) {
	    console.log("message", evt);
	    if (evt && evt.data) {
		var obj = JSON.decode(evt.data);
		if (obj.tid == '0' || obj.tid == this._theaterId) {
		    if (obj.type == NetflixTheater.MessageType.Message) {
			this.showMessage(obj);
		    }
		}
	    }
        }.bind(this);
	
	this._createMsgContainer = new Element('div', {
	    id: 'createMsgContainer',
	    styles: { float: 'left' }
	});
	document.body.insertBefore(this._createMsgContainer, document.body.firstChild);
    },

    run: function() {
	var createMsg = new Element('div', {
	    id: 'createMsg',
	    html: '+',
	    styles: { width: '50px', height: '50px', fontSize: '25px', color: 'white', float: 'left' },
	    events: { 
		click: NetflixTheater.Application.getCurrent().createMessage.bind(this), 
		mouseover: function() {
		    $('createMsg').setStyle('cursor', 'pointer');
		},
		mouseout: function() {
		    $('createMsg').setStyle('cursor', 'default');
		}
	    }
	});
	this._createMsgContainer.appendChild(createMsg);

	var bar = new Element('div', {
	    id: 'overlay'
	});
	var imgUrl = new Element('img', {
	    id: 'imgAvatarOverlay',
	    src: chrome.extension.getURL('images/mstk_back.png'),
	    styles: { width: $('SLPlayer').getSize().x + 'px' }
	});
	bar.appendChild(imgUrl);
	document.body.appendChild(bar);
    },

    createMessage: function(e) {
	if (this._msgBox == null) {
	    this._msgBox = new Element('input', {
		id: 'msgBox',
		styles: { width: '200px', backgroundColor: '#ffffff', float: 'left' },
		events: { keypress :
			  function(e) {
			      if (e.code == NetflixTheater.KeyCodes.Enter) {
				  this.sendMessage($('msgBox').get('value'));
			      }
			  }.bind(this)
			}
	    });
	    this._createMsgContainer.appendChild(this._msgBox);
	} else {
	    this._msgBox.set('value', '');
	    this._msgBox.style.display = 'block';
	}
	this._msgBox.focus();
    },

    sendMessage: function(message) {
	var encoded = JSON.encode({
	    id : this._userId,
	    tid : this._theaterId,
	    type : NetflixTheater.MessageType.Message,
	    msg : message
	});
	_conn.send(encoded);
	this._msgBox.set('value', '');
    },

    showMessage: function(msgObj) {
	var leftPos = this.getLeftBubblePosition();
	var bubble = new Element('div', {
	    id: 'comment',
	    html: '<strong>' + msgObj.id + '&emsp;says</strong><br/><br/>' + msgObj.msg,
	    styles: { 
		backgroundImage: 'url("' + chrome.extension.getURL('images/square_bubble_small.png') + '")',
		backgroundRepeat: 'no-repeat',
		position: 'absolute',
		width: '85px',
		height: '150px',
		padding: '5px',
		bottom: '85px',
		left: leftPos, 
		zIndex: '1000'
	    }
	});
	document.body.appendChild(bubble);
	bubble.set('tween', { duration: 5000}).tween('opacity', 0).set('value', '');
	this._msgBox.set('display', 'none');
    },

    getLeftBubblePosition: function() {
	return Math.floor(Math.random() * 500 + 300) + 'px';
    }

});
NetflixTheater._Application_instance = null;

NetflixTheater.Application.getCurrent = function NetflixTheater_Application$current() {
    if (!NetflixTheater._Application_instance) {
	NetflixTheater._Application_instance = new NetflixTheater._Application();
    }
    return NetflixTheater._Application_instance;
}

NetflixTheater.KeyCodes = {};
NetflixTheater.KeyCodes.Enter = 13;
NetflixTheater.MessageType = {};
NetflixTheater.MessageType.Message = "message";
NetflixTheater.QueryParams = {};
NetflixTheater.QueryParams.TheaterId = "theater-id";
NetflixTheater.QueryParams.UserId = "user-id";

if (window.location.pathname == '/WiPlayer') {
    NetflixTheater.Application.getCurrent().run();
}

