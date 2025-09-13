import React from 'react';
import { Container, Card, Alert } from 'react-bootstrap';

const ImportStudents = () => {
    return (
        <Container fluid>
            <Card>
                <Card.Body>
                    <Alert variant="info">
                        <i className="fas fa-info-circle me-2"></i>
                        批量导入学生功能正在开发中，敬请期待！
                    </Alert>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default ImportStudents;
